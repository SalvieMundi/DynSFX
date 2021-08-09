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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.openal.EXTEfx;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

public class Reverb {
	private static int id = -1;
	private static int slot = -1;

	private static boolean enabled = false;
	private static int tickCount = 0;
	private static float prevDecayFactor = 0.0f;
	private static float prevRoomFactor = 0.0f;
	private static float prevSkyFactor = 0.0f;

	private static float density = 0.2f;
	private static float diffusion = 0.6f;
	private static float gain = 0.15f;
	private static float gainHF = 0.8f;
	private static float decayTime = 0.1f;
	private static float decayHFRatio = 0.7f;
	private static float reflectionsGain = 0.0f;
	private static float reflectionsDelay = 0.0f;
	private static float lateReverbGain = 0.0f;
	private static float lateReverbDelay = 0.0f;
	private static float airAbsorptionGainHF = 0.99f;
	private static float roomRolloffFactor = 0.0f;
	private static int decayHFLimit = 1;
	
	//TODO: configurable
	private static List<Material> HIGH_REVERB_MATERIALS = Arrays.asList(Material.STONE, Material.GLASS, Material.ICE, Material.DENSE_ICE, Material.METAL);
	private static List<Material> LOW_REVERB_MATERIALS = Arrays.asList(Material.WOOL, Material.CARPET, Material.LEAVES, Material.PLANT, Material.UNDERWATER_PLANT, Material.REPLACEABLE_PLANT, Material.REPLACEABLE_UNDERWATER_PLANT, Material.SOLID_ORGANIC, Material.GOURD, Material.CACTUS, Material.COBWEB, Material.CAKE, Material.SPONGE, Material.SNOW_LAYER, Material.SNOW_BLOCK, Material.WOOD);

	public static void reinit() {
		id = EXTEfx.alGenEffects();
		slot = EXTEfx.alGenAuxiliaryEffectSlots();
	}
	
	public static void updateGlobal(final boolean verdict, final MinecraftClient client, final ConfigData data, final Vec3d clientPos) {
		if (verdict) update(client, data, clientPos);
		else reset(data);
	}

	public static boolean updateSoundInstance(final SoundInstance soundInstance) {
		// ensure sound id is valid
		if (id == -1) {
			reinit();
		}
		// if not needed, exit
		if (!enabled || (reflectionsDelay <= 0 && lateReverbDelay <= 0) )return false;

		if (soundInstance.getAttenuationType() == SoundInstance.AttenuationType.LINEAR) {
			roomRolloffFactor = 2f / (Math.max(soundInstance.getVolume(), 1f) + 2f);
		} else {
			roomRolloffFactor = 0f;
		}

		density = 				Utils.clamp(density);
		diffusion = 			Utils.clamp(diffusion);
		gain = 					Utils.clamp(gain);
		gainHF = 				Utils.clamp(gainHF);
		decayTime = 			Utils.clamp(decayTime);
		decayHFRatio = 			Utils.clamp(decayHFRatio);
		reflectionsGain = 		Utils.clamp(reflectionsGain);
		reflectionsDelay = 		Utils.clamp(reflectionsDelay);
		lateReverbGain = 		Utils.clamp(lateReverbGain);
		lateReverbDelay = 		Utils.clamp(lateReverbDelay);
		airAbsorptionGainHF = 	Utils.clamp(airAbsorptionGainHF);
		roomRolloffFactor = 	Utils.clamp(roomRolloffFactor);
		decayHFLimit = 			Utils.clamp(decayHFLimit);

		EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 0);
		EXTEfx.alEffecti(id, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_REVERB);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DENSITY, density);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DIFFUSION, diffusion);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_GAIN, gain);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_GAINHF, gainHF);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DECAY_TIME, decayTime);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_DECAY_HFRATIO, decayHFRatio);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_GAIN, reflectionsGain);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_REFLECTIONS_DELAY, reflectionsDelay);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_GAIN, lateReverbGain);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_LATE_REVERB_DELAY, lateReverbDelay);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF, airAbsorptionGainHF);
		EXTEfx.alEffectf(id, EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR, roomRolloffFactor);
		EXTEfx.alEffecti(id, EXTEfx.AL_REVERB_DECAY_HFLIMIT, decayHFLimit);
		EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_EFFECT, id);
		EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 1);

		return true;
	}

	public static int getSlot() {
		return slot;
	}

	private static void reset(final ConfigData data) {
		enabled = false;
		density = data.reverbFilter.density;
		diffusion = data.reverbFilter.diffusion;
		gain = data.reverbFilter.gain;
		gainHF = data.reverbFilter.gainHF;
		decayTime = data.reverbFilter.minDecayTime;
		decayHFRatio = data.reverbFilter.decayHFRatio;
		reflectionsGain = 0;
		reflectionsDelay = 0;
		lateReverbGain = 0;
		lateReverbDelay = 0;
		airAbsorptionGainHF = data.reverbFilter.airAbsorptionGainHF;
		roomRolloffFactor = 0.0f;
	}

	private static void update(final MinecraftClient client, final ConfigData data, final Vec3d clientPos) {
		enabled = data.reverbFilter.enabled;
		final int maxBlocks = data.reverbFilter.maxBlocks;
		final boolean checkSky = data.reverbFilter.checkSky;
		final float reverbPercent = data.reverbFilter.reverbPercent;
		final float minDecayTime = data.reverbFilter.minDecayTime;
		final float reflectionGainBase = data.reverbFilter.reflectionsGainBase;
		final float reflectionGainMultiplier = data.reverbFilter.reflectionsGainMultiplier;
		final float reflectionDelayMultiplier = data.reverbFilter.reflectionsDelayMultiplier;
		final float lateReverbGainBase = data.reverbFilter.lateReverbGainBase;
		final float lateReverbGainMultiplier = data.reverbFilter.lateReverbGainMultiplier;
		final float lateReverbDelayMultiplier = data.reverbFilter.lateReverbDelayMultiplier;

		// get base reverb
		final Identifier dimension = client.world.getRegistryKey().getValue();
		float decayFactor = data.reverbFilter.getDimensionBaseReverb(dimension);

		if (enabled && tickCount++ == 20) {
			tickCount = 0;

			// scan surroundings
			final BlockPos playerPos = new BlockPos(clientPos);

			// initialize sample variables
			Random random = new Random();
			Set<BlockPos> visited = new TreeSet<>();
			List<BlockState> blocksFound = new ArrayList<>();
			List<BlockPos> toVisit = new LinkedList<>();

			// sample random blocks in surroundings
			toVisit.add(playerPos);
			for (int i = 0; i < maxBlocks && !toVisit.isEmpty(); ++i) {
				final BlockPos current = toVisit.remove(random.nextInt(toVisit.size() ));
				visited.add(current);
				for(Direction direction : Direction.values() ){
					final BlockPos pos = current.offset(direction);
					final BlockState blockState = client.world.getBlockState(pos);
					final Material material = blockState.getMaterial();
					if (!material.blocksMovement() ){
						if (!visited.contains(pos) && !toVisit.contains(pos) ){
							toVisit.add(pos);
						}
						if (!blockState.isAir() && material != Material.WATER && material != Material.LAVA) {
							blocksFound.add(blockState);
						}
					} else {
						blocksFound.add(blockState);
					}
				}
			}

			// calculate decay factor
			double highReverb = 0.0;
			double midReverb = 0.0;
			double lowReverb = 0.0;
			for (BlockState blockState : blocksFound) {
				// custom block reverb overrides
				final ReverbInfo customReverb = data.reverbFilter.getCustomBlockReverb(Registry.BLOCK.getId(blockState.getBlock() ));
				if (customReverb != null) {
					switch(customReverb) {
					case HIGH:
						highReverb += 1.0;
						break;
					case LOW:
						lowReverb += 1.0;
						break;
					case MID:
					default:
						midReverb += 1.0;
						break;
					}
				} else {
					// material based reverb
					if (HIGH_REVERB_MATERIALS.contains(blockState.getMaterial() )) {
						highReverb += 1;
					} else if (LOW_REVERB_MATERIALS.contains(blockState.getMaterial() )) {
						lowReverb += 1;
					} else {
						midReverb += 1;
					}
				}
			}
			// I have the skills, uH, to pay the bills, uH
			// but seriously, when using all the same type, be smart about it!
			if (highReverb + midReverb + lowReverb > 0d) {
				decayFactor += (highReverb - lowReverb) / (highReverb + midReverb + lowReverb);
			}
			decayFactor = Utils.clamp(decayFactor);

			// calculate room factor
			final int roomSize = visited.size();
			float roomFactor = roomSize / (float) maxBlocks;

			// calculate sky factor
			float skyFactor = 0;
			if (checkSky && roomSize == maxBlocks) {
				if (hasSkyAbove(client.world, playerPos) )skyFactor += 1;
				final Direction[] directions = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
				for(Direction direction : directions) {
					if (hasSkyAbove(client.world, playerPos.offset(direction, random.nextInt(5) + 5) )) skyFactor += 1;
					if (hasSkyAbove(client.world, playerPos.offset(direction, random.nextInt(5) + 5).offset(Direction.UP, 5) )) skyFactor += 1;
				}
			}
			skyFactor = 1f - skyFactor / 9f;
			skyFactor *= skyFactor;



			// interpolate values
			decayFactor = (decayFactor + prevDecayFactor) / 2f;
			roomFactor = (roomFactor + prevRoomFactor) / 2f;
			skyFactor = (skyFactor + prevSkyFactor) / 2f;
			prevDecayFactor = decayFactor;
			prevRoomFactor = roomFactor;
			prevSkyFactor = skyFactor;

			// update values
			decayTime = reverbPercent * 6f * decayFactor * roomFactor * skyFactor;
			if (decayTime < minDecayTime) {
				decayTime = minDecayTime;
			}
			reflectionsGain = reverbPercent * (reflectionGainBase + reflectionGainMultiplier * roomFactor);
			reflectionsDelay = reflectionDelayMultiplier * roomFactor;
			lateReverbGain = reverbPercent * (lateReverbGainBase + lateReverbGainMultiplier * roomFactor);
			lateReverbDelay = lateReverbDelayMultiplier * roomFactor;
			//System.out.println(lowReverb + " " + midReverb + " " + highReverb + "\n");
		}
	}

	private static boolean hasSkyAbove(final ClientWorld world, final BlockPos pos) {
		if (world.getDimension().hasCeiling() )return false;
		
		final Chunk chunk = world.getChunk(pos);
		final Heightmap heightMap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
		int x = pos.getX() - chunk.getPos().getStartX();
		int z = pos.getZ() - chunk.getPos().getStartZ();
		x = Math.max(0, Math.min(x, 15) );
		z = Math.max(0, Math.min(z, 15) );
		return heightMap != null && heightMap.get(x, z) <= pos.getY();
	}
	
	public static enum ReverbInfo {
		HIGH,
		MID,
		LOW;
		
		public static ReverbInfo fromName(String name) {
			name = name.toUpperCase();
			return ReverbInfo.valueOf(name);
		}
	}
}
