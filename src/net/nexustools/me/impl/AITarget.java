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

package net.nexustools.me.impl;

/**
 *
 * @author katelyn
 */
public interface AITarget {
	
	public enum TickReason {
		// Hooks
		GameTick,
		FrameRender,
		
		// Interactions
		TargetChanged,
		TouchedEntity,
		
		// Holding Changes
		InventoryChanged,
		ItemChanged,
		
		// State Changes
		Replaced,
		Destroyed,
		
		//Environment Oriented
		WeatherChanged,
		SunlightChanged,
		LightChanged,
	}
	
	public AI AI();
	public void setAI(AI ai);
	
	public void registerTick(AI ai, TickReason action);
	
}
