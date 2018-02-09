package Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisCache {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final long DEFAULT_REDIS_TIME_TO_LIVE_SECONDS = 7200;// 缓存设置时间

	@Resource
	protected RedisTemplate<String, String> redisTemplate;

	/**
	 * 添加缓存数据
	 *
	 * @param key
	 * @param obj
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public <T> boolean putCache(String key, T obj) throws Exception {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = SerializerUtil.serializeObj(obj);
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.setNX(bkey, bvalue);
			}
		});
		return result;
	}

	/**
	 * 添加缓存数据，设定缓存失效时间
	 *
	 * @param key
	 * @param obj
	 * @param <T>
	 * @throws Exception
	 */
	public <T> void putCacheWithExpireTime(String key, T obj) throws Exception {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = SerializerUtil.serializeObj(obj);
		redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.setEx(bkey, DEFAULT_REDIS_TIME_TO_LIVE_SECONDS, bvalue);
				return true;
			}
		});
	}

	/**
	 * 根据key取缓存数据
	 *
	 * @param key
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public <T> T getCache(final String key) throws Exception {
		byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.get(key.getBytes());
			}
		});
		if (result == null) {
			return null;
		}

		try {
			return (T) SerializerUtil.deserializeObj(result);
		} catch (Exception e) {
			logger.error("getCache fail : key:{}", key, e.getMessage(), e);
		}
		return null;
	}

	public void remove(final String key) {
		redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.del(key.getBytes());
				return true;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean del(final String keys) {
		try {
			Object result = redisTemplate.execute(new RedisCallback() {
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					long result = 0;
					result = connection.del(keys.getBytes());
					return result;
				}
			});
			if (result.equals(0L)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
