/*
 * Copyright (c) 2021 Andr? Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.andre111.dynamicsf.filter;

import me.andre111.dynamicsf.config.ConfigData;
import me.andre111.dynamicsf.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.datafixers.util.Pair;

// import org.lwjgl.openal.EXTEfx;

// import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;

public class Echo {
	private static boolean enabled = false;
	private static boolean doAverage = true;
	private static int ignore = 0;

	private static Vec3d newPos = new Vec3d(0,0,0);
	private static Vec3d furthest = new Vec3d(0,0,0);
	private static double distance = 0;
	private static int size = 2;
	private static int positionCount = 0;

	private static float decay = 0;
	private static float sky = 0;
	private static float idVol = 0.999999999999969f;

	private static int counter = 0;


	private static Vec3d prevPos = new Vec3d(0,0,0);
	private static String prevName = "";


	// TODO remove later
	private static double increment = 0;

	// [[timer,soundInstance], ]
	private static List<Pair<Float,SoundInstance>> sounds = new ArrayList<>();

	public static boolean getEnabled() {
		return enabled;
	}

	public static boolean getDoAverage() {
		return doAverage;
	}

	public static void reset(final ConfigData data) {
		enabled = data.echoFilter.enabled;
		doAverage = data.echoFilter.doAverage;
		sounds = new ArrayList<>();
	}

	public static boolean updateSoundInstance(final SoundInstance soundInstance) {
		if (!enabled) {
			sounds = new ArrayList<>();
			return false;
		}

		if (ignore <= 0) {

			final Vec3d currentPos = new Vec3d(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
			String currentName = soundInstance.getId().toString();
			String counterName = Integer.toString(counter / 100);

			// MANY random noises, in rapid succession, do this to make
			// it manageable, TODO make dynamically configurable
			// if (currentName.toString().startsWith("presencefootsteps")) {
				// currentName = counterName;
				// counter = (counter + 1) % 1000; // cycle 10x around
			// }

			// if (
			// 	// if it's a duplicate, for sure
			// 	// (prevName == counterName && currentName == counterName) ||
			// 	// otherwise, ensure that it's a duplicate
			// 	(currentPos.distanceTo(prevPos) < 3f && prevName == currentName)
			// ) {
			// 	System.out.print("|");
			// } else
			
			// why does this never work?
			if (prevName == currentName || currentPos.distanceTo(prevPos) < 2) {
				prevPos = currentPos;
				prevName = currentName;
				return false;
			}
			prevPos = currentPos;
			prevName = currentName;
			if (soundInstance.getVolume() != idVol) {
				System.out.println("\t" + prevName + "\t" + currentName + "\t" + currentPos.distanceTo(prevPos));
				
				sounds.add(new Pair<Float,SoundInstance>(100f,soundInstance));
				ignore++;
			}
		}
		else {
			ignore--;
		};

		return true;
	}

	public static void updateStats(final ConfigData data, final Vec3d clientPos,
		final List<Vec3d> positions, final float decay, final float sky
	) {
		enabled = data.echoFilter.enabled;
		doAverage = data.echoFilter.doAverage;

		if (!enabled) return;

		// System.out.println("\n\n\n?");

		newPos = clientPos;
		furthest = clientPos;
		distance = 0;
		size = 2;

		// find furthest position
		for (Vec3d position : positions) {
			double newDistance = clientPos.distanceTo(position);
			if (newDistance > distance) {
				distance = newDistance;
				furthest = position;
			}
		}

		// average ? add values for mean()
		if (doAverage) {
			for (Vec3d position : positions) {
				newPos = newPos.add(position);
				size++;
			}
		}

		// evaluate average
		newPos = newPos.add(furthest).multiply(1f / size);
		positionCount = positions.size();
		prevName = "";

	}

	public static void update(final MinecraftClient client) {
		// System.out.println("\n\n\n\t\t\t\t" + sounds.size());
		if (sounds.size() == 0) return;

		int i = 0;
		// increment values
		while (i < sounds.size()) {
			final Pair<Float,SoundInstance> instance = sounds.get(i);
			final SoundInstance sound = instance.getSecond();

			float timer = instance.getFirst();
			// if timer's still going
			if (timer > 0) {
				// timer -= decay + (decay * sky);
				// decay
				timer *= Utils.clamp(decay);
				// sky-based decay
				// timer -= sky * ( 0.75 - decay );
				// timer -= sky / positionCount;
				// max of 5s, subtract based on distance
				// increment = -1 * Math.max(2, Math.abs((positionCount * positionCount) - distance));
				// increment = Math.abs( Math.max( 0.1, Math.max(1,distance / 3) / Math.max(1,positionCount)) );
				// timer -= increment;
				timer--;
				// System.out.println(timer);
				// decrement timer
				// timer /= Math.max(1, distance / positionCount);

				if (timer <= 0) {

				}

				// update timer value
				sounds.set(i, new Pair<Float,SoundInstance>(timer, sound));

				// don't loop infinitely!
				i++;

			} else {
				// ensure the sound's ignored by echo
				ignore++;
				// play sound, useDistance=false - no further delay
				client.world.playSound(newPos.getX(), newPos.getY(), newPos.getZ(), new SoundEvent(sound.getId()), sound.getCategory(), idVol, 1f, false);
				// remove the sound
				sounds.remove(i);

				// debug
				// System.out.println("\n\n\nECHO\n\n\n");
				// System.out.print(sound.getId() + "\t\t");
				// System.out.println(increment);

				// don't increment here, as a sound's removed
			}
		}
	}
}
