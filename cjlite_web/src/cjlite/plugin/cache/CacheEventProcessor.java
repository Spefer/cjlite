/**
 * 
 */
package cjlite.plugin.cache;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author YunYang
 * @version Oct 29, 2015 4:52:22 PM
 */
class CacheEventProcessor extends Thread {

	private final AbstractCacheManager abstractCacheManager;
	private ConcurrentLinkedQueue<CacheEvent> eventQueue = new ConcurrentLinkedQueue<CacheEvent>();

	private boolean running = false;

	public CacheEventProcessor(AbstractCacheManager abstractCacheManager) {
		super("CacheEventProcessor");
		this.abstractCacheManager = abstractCacheManager;
	}

	public void fireEvent(List<CacheEvent> event) {
		eventQueue.addAll(event);
		if (!running) {
			running = true;
			this.start();
		}
	}

	@Override
	public void interrupt() {
		running = false;
		super.interrupt();
	}

	@Override
	public void run() {
		while (running) {
			CacheEvent event = eventQueue.poll();
			if (event != null) {

				List<CacheElement> elements = abstractCacheManager.getCacheElements(event.getName(),
						event.getNameMatcher());

				for (CacheElement element : elements) {
					element.listen(event);
				}

			}
		}
	}

}
