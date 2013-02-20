/**
   Copyright [2013-2018] [plter] http://plter.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.plter.lib.java.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.plter.lib.java.utils.LogFactory;


/**
 * 对象池
 * @author xtiqin
 *
 */
public class ObjectPool {

	private static final Logger log = LogFactory.getLogger();


	private static final Map<Class<?>, List<Object>> classMap=new ConcurrentHashMap<Class<?>,List<Object>>();
	private static List<Object> tmpArr=null;


	/**
	 * 取得指定对象的实例
	 * @param clazz
	 * @return
	 */
	public final synchronized static <T> T get(Class<T> clazz){

		try {
			tmpArr=classMap.get(clazz);
			if (tmpArr!=null) {
				if (tmpArr.size()>0) {
					return (T)tmpArr.remove(0);
				}
			}

			log.config("Create a instance of ".concat(clazz.toString()));

			return (T)clazz.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * 回收一个对象
	 * @param obj
	 */
	public final synchronized static void recycle(Object obj){
		tmpArr=classMap.get(obj.getClass());

		if (tmpArr!=null) {
			if (!tmpArr.contains(obj)) {
				tmpArr.add(obj);
			}
		}else{
			tmpArr=Collections.synchronizedList(new ArrayList<>());
			tmpArr.add(obj);
			classMap.put(obj.getClass(), tmpArr);
		}
	}

	public final static void clear(){
		tmpArr=null;
		classMap.clear();
	}


}