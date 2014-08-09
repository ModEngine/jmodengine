/*
 * jmodengine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 or any later version.
 * 
 * jmodengine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with jmodengine.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */


package net.nexustools.me.platform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.nexustools.concurrent.PropList;
import net.nexustools.concurrent.PropMap;
//import net.nexustools.io.format.StreamReader;
import net.nexustools.me.impl.Base;
import net.nexustools.runtime.ThreadedRunQueue;
import net.nexustools.utils.Creator;

/**
 *
 * @author katelyn
 * 
 */
public abstract class MEPlatform extends ThreadedRunQueue {
	
	private static final ThreadLocal<MEPlatform> current = new ThreadLocal();
	private static final PropMap<Class<? extends MEPlatform>, MEPlatform> platformsByClass = new PropMap();
	private static final PropMap<String, MEPlatform> platformsByName = new PropMap();
	private static final PropList<MEPlatform> allPlatforms = new PropList();
	private static boolean needScanPlatforms = true;
	
	public static MEPlatform current() {
		return current.get();
	}
	
	public static void scanPlatforms() {
		needScanPlatforms = false;
	}
	public static void register(MEPlatform platform) {
		if(allPlatforms.unique(platform)) {
			platformsByName.put(platform.name(), platform);
			platformsByClass.put(platform.getClass(), platform);
		}
	}
	
	public static MEPlatform byName(String name) {
		if(needScanPlatforms)
			scanPlatforms();
		return platformsByName.get(name);
	}
	public static MEPlatform byClass(Class<? extends MEPlatform> pClass) {
		MEPlatform platform = platformsByClass.get(pClass);
		if(platform == null) {
			try {
				platform = pClass.newInstance();
				register(platform);
			} catch (InstantiationException ex) {
				throw new RuntimeException(ex);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
		}
		return platform;
	}
	public static MEPlatform best(Feature[] desired) {
		return best(desired, new Feature[0]);
	}
	public static MEPlatform best(Feature[] desired, Feature[] required) {
		if(needScanPlatforms)
			scanPlatforms();
		
		List<MEPlatform> compatible = allPlatforms.copy();
		Iterator<MEPlatform> it = compatible.iterator();
		while(it.hasNext()) {
			MEPlatform platform = it.next();
			for(Feature req : required)
				if(!platform.supports(req)) {
					it.remove();
					break;
				}
		}
		// TODO: Sort by desire list
		return compatible.get(0); 
	}
	
	public static enum Feature {
		// TODO: Populate
	}
	
	public static interface BaseRegistry {
		public <B extends Base, P extends MEPlatform> void add(Class<B> type, Creator<B, P> creator);
	}
	
	private final HashMap<Class<?>, Creator> typeMap = new HashMap();
	protected MEPlatform(String name) {
        super(name + "-RunQueue");
        System.out.println("Spawning Platform `" + name + '`');
		populate(new BaseRegistry() {
			public <B extends Base, P extends MEPlatform> void add(Class<B> type, Creator<B, P> creator) {
				typeMap.put(type, creator);
			}
		});
        makeCurrent();
	}
	
	protected abstract void populate(BaseRegistry baseRegistry);
	
	public final <B extends Base> B create(Class<B> type) throws MEUnsupportedBaseType{
		Creator<B, MEPlatform> creator = typeMap.get(type);
		if(creator == null)
			throw new MEUnsupportedBaseType();
		return creator.create(this);
	}
	public final Base parse(String path) throws MEPlatformException{
		throw new UnsupportedOperationException("Not yet supported.");
	}
//	public final Base parse(StreamReader processor) throws MEPlatformException{
//		throw new UnsupportedOperationException("Not yet supported.");
//	}
	
	public abstract boolean supports(Feature feature);
	
	@Override
	public final void makeCurrent() {
		current.set(this);
	}
    
}
