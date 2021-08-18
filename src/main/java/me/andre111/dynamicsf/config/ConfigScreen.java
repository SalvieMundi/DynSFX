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
package me.andre111.dynamicsf.config;

import java.util.ArrayList;
//import java.util.Arrays;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.TranslatableText;

// should probably abstract this stuff, eh, later!
// use this:
// import java.util.function.Function;

public class ConfigScreen implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		ConfigData data = Config.getData();
		ConfigDataLiquid liquid = new ConfigDataLiquid();
		ConfigDataObstruction obstruction = new ConfigDataObstruction();
		ConfigDataReverb reverb = new ConfigDataReverb();
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create();
			builder.setParentScreen(parent);
			builder.setTitle(new TranslatableText("dynamicsoundfilters.config.title") );
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			builder.setSavingRunnable(Config::saveData);

			ConfigCategory generalCat = builder
					.getOrCreateCategory(new TranslatableText("dynamicsoundfilters.config.general") );
			{
				generalCat.addEntry(entryBuilder
						.startStrList(new TranslatableText("dynamicsoundfilters.config.general.ignored"),
								data.general.ignoredSoundEvents)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.general.ignored.tooltip") )
						.setDefaultValue(ConfigDataGeneral.DEFAULT_IGNORED_SOUND_EVENTS).setSaveConsumer(l -> {
							data.general.ignoredSoundEvents = l;
							data.general.recalculateCache();
						}).build() );
			}

			ConfigCategory liquidFilterCat = builder
					.getOrCreateCategory(new TranslatableText("dynamicsoundfilters.config.liquid") );
			{
				liquidFilterCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("dynamicsoundfilters.config.liquid.enable"),
								data.liquidFilter.enabled)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.enable.tooltip") )
						.setDefaultValue(true).setSaveConsumer(b -> data.liquidFilter.enabled = b).build() );

				SubCategoryBuilder waterCat = entryBuilder
						.startSubCategory(new TranslatableText("dynamicsoundfilters.config.liquid.water") );
				{
					waterCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gain"),
									data.liquidFilter.waterGain)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gain.tooltip") )
							// defaults
							.setDefaultValue(liquid.getWaterGain()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.liquidFilter.waterGain = f).build() );
					waterCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf"),
									data.liquidFilter.waterGainHF)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf.tooltip") )
							// defaults
							.setDefaultValue(liquid.getWaterGainHF()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.liquidFilter.waterGainHF = f).build() );
				}
				liquidFilterCat.addEntry(waterCat.build() );

				SubCategoryBuilder lavaCat = entryBuilder
						.startSubCategory(new TranslatableText("dynamicsoundfilters.config.liquid.lava") );
				{
					lavaCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gain"),
									data.liquidFilter.lavaGain)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gain.tooltip") )
							// defaults
							.setDefaultValue(liquid.getLavaGain()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.liquidFilter.lavaGain = f).build() );
					lavaCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf"),
									data.liquidFilter.lavaGainHF)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf.tooltip") )
							// defaults
							.setDefaultValue(liquid.getLavaGainHF()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.liquidFilter.lavaGainHF = f).build() );
				}
				liquidFilterCat.addEntry(lavaCat.build() );
			}

			ConfigCategory reverbFilterCat = builder
					.getOrCreateCategory(new TranslatableText("dynamicsoundfilters.config.reverb") );
			{
				reverbFilterCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("dynamicsoundfilters.config.reverb.enable"),
								data.reverbFilter.enabled)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.enable.tooltip") )
						.setDefaultValue(true).setSaveConsumer(b -> data.reverbFilter.enabled = b).build() );

				reverbFilterCat.addEntry(entryBuilder
						.startFloatField(new TranslatableText("dynamicsoundfilters.config.reverb.percent"),
								data.reverbFilter.reverbPercent)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.percent.tooltip") )
						// defaults
						.setDefaultValue(reverb.getReverbPercent()).setMin(0f).setMax(2f)
						.setSaveConsumer(f -> data.reverbFilter.reverbPercent = f).build() );

				reverbFilterCat.addEntry(entryBuilder
						.startStrList(new TranslatableText("dynamicsoundfilters.config.reverb.dimensions"),
								data.reverbFilter.dimensionBaseReverb)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip1"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip2"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip3"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip4") )
						// defaults
						// did I do it right?
						.setDefaultValue( reverb.getDimensionBaseReverb() ).setSaveConsumer(l -> {
							data.reverbFilter.dimensionBaseReverb = l;
							data.reverbFilter.recalculateCache();
						}).build() );
				reverbFilterCat.addEntry(entryBuilder
						.startStrList(new TranslatableText("dynamicsoundfilters.config.reverb.blocks"),
								data.reverbFilter.customBlockReverb)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip1"),
								new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip2"),
								new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip3") )
						// defaults
						.setDefaultValue(new ArrayList<>() ).setSaveConsumer(l -> {
							data.reverbFilter.customBlockReverb = l;
							data.reverbFilter.recalculateCache();
						}).build() );

				SubCategoryBuilder scannerCat = entryBuilder
						.startSubCategory(new TranslatableText("dynamicsoundfilters.config.reverb.scanner") );
				scannerCat.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.scanner.tooltip") );
				{
					scannerCat.add(entryBuilder
							.startIntField(new TranslatableText("dynamicsoundfilters.config.reverb.scanner.blocks"),
									data.reverbFilter.maxBlocks)
							.setTooltip(
									new TranslatableText("dynamicsoundfilters.config.reverb.scanner.blocks.tooltip") )
							// defaults
							.setDefaultValue(reverb.getMaxBlocks()).setMin(0).setMax(Integer.MAX_VALUE)
							.setSaveConsumer(i -> data.reverbFilter.maxBlocks = i).build() );

					scannerCat.add(entryBuilder
							.startBooleanToggle(
									new TranslatableText("dynamicsoundfilters.config.reverb.scanner.checksky"),
									data.reverbFilter.checkSky)
							.setTooltip(
									new TranslatableText("dynamicsoundfilters.config.reverb.scanner.checksky.tooltip") )
							// defaults
							.setDefaultValue(reverb.getCheckSky()).setSaveConsumer(b -> data.reverbFilter.checkSky = b)
							.build() );
				}
				reverbFilterCat.addEntry(scannerCat.build() );

				SubCategoryBuilder advancedCat = entryBuilder
						.startSubCategory(new TranslatableText("dynamicsoundfilters.config.reverb.advanced") );
				advancedCat.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.advanced.tooltip") );
				{
					advancedCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.reverb.advanced.density"),
									data.reverbFilter.density)
							// defaults
							.setDefaultValue(reverb.getDensity()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.reverbFilter.density = f).build() );
					advancedCat
							.add(entryBuilder
									.startFloatField(
											new TranslatableText(
													"dynamicsoundfilters.config.reverb.advanced.diffusion"),
											data.reverbFilter.diffusion)
									.setDefaultValue(0.6f).setMin(0.0f).setMax(1.0f)
									.setSaveConsumer(f -> data.reverbFilter.diffusion = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.reverb.advanced.gain"),
									data.reverbFilter.gain)
							// defaults
							.setDefaultValue(reverb.getGain()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.reverbFilter.gain = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.reverb.advanced.gainhf"),
									data.reverbFilter.gainHF)
							// defaults
							.setDefaultValue(reverb.getGainHF()).setMin(0f).setMax(1f)
							.setSaveConsumer(f -> data.reverbFilter.gainHF = f).build() );
					advancedCat
							.add(entryBuilder
									.startFloatField(
											new TranslatableText(
													"dynamicsoundfilters.config.reverb.advanced.mindecaytime"),
											data.reverbFilter.minDecayTime)
									// defaults
									.setDefaultValue(reverb.getMinDecayTime()).setMin(0.1f).setMax(20f)
									.setSaveConsumer(f -> data.reverbFilter.minDecayTime = f).build() );
					advancedCat
							.add(entryBuilder
									.startFloatField(
											new TranslatableText(
													"dynamicsoundfilters.config.reverb.advanced.decayhfratio"),
											data.reverbFilter.decayHFRatio)
									// defaults
									.setDefaultValue(reverb.getDecayHFRatio()).setMin(0.1f).setMax(20f)
									.setSaveConsumer(f -> data.reverbFilter.decayHFRatio = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.airabsorptiongainhf"),
									data.reverbFilter.airAbsorptionGainHF)
							// defaults
							.setDefaultValue(reverb.getAirAbsorptionGainHF()).setMin(0.892f).setMax(1f)
							.setSaveConsumer(f -> data.reverbFilter.airAbsorptionGainHF = f).build() );

					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsgainbase"),
									data.reverbFilter.reflectionsGainBase)
							// defaults
							.setDefaultValue(reverb.getReflectionsGainBase()).setMin(0f).setMax(1.58f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsGainBase = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsgainmultiplier"),
									data.reverbFilter.reflectionsGainMultiplier)
							// defaults
							.setDefaultValue(reverb.getReflectionsGainMultiplier()).setMin(0f).setMax(1.58f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsGainMultiplier = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsdelaymultiplier"),
									data.reverbFilter.reflectionsDelayMultiplier)
							// defaults
							.setDefaultValue(reverb.getReflectionsDelayMultiplier()).setMin(0f).setMax(0.3f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsDelayMultiplier = f)
							.build() );

					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbgainbase"),
									data.reverbFilter.lateReverbGainBase)
							// defaults
							.setDefaultValue(reverb.getLateReverbGainBase()).setMin(0f).setMax(5f)
							.setSaveConsumer(f -> data.reverbFilter.lateReverbGainBase = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbgainmultiplier"),
									data.reverbFilter.lateReverbGainMultiplier)
							// defaults
							.setDefaultValue(reverb.getLateReverbGainMultiplier()).setMin(0f).setMax(5f)
							.setSaveConsumer(f -> data.reverbFilter.lateReverbGainMultiplier = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbdelaymultiplier"),
									data.reverbFilter.lateReverbDelayMultiplier)
							// defaults
							.setDefaultValue(reverb.getLateReverbDelayMultiplier()).setMin(0f).setMax(0.1f)
							.setSaveConsumer(f -> data.reverbFilter.lateReverbDelayMultiplier = f).build() );
				}
				reverbFilterCat.addEntry(advancedCat.build() );
			}

			ConfigCategory obstructionFilterCat = builder
					.getOrCreateCategory(new TranslatableText("dynamicsoundfilters.config.obstruction") );
			{
				obstructionFilterCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("dynamicsoundfilters.config.obstruction.enable"),
								data.obstructionFilter.enabled)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.obstruction.enable.tooltip") )
						// defaults
						.setDefaultValue(reverb.getEnabled()).setSaveConsumer(b -> data.obstructionFilter.enabled = b)
						.build() );

				obstructionFilterCat.addEntry(entryBuilder
						.startFloatField(new TranslatableText("dynamicsoundfilters.config.obstruction.step"),
								data.obstructionFilter.obstructionStep)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.obstruction.step.tooltip") )
						// defaults
						.setDefaultValue(obstruction.getObstructionStep()).setMin(0f).setMax(1f)
						.setSaveConsumer(f -> data.obstructionFilter.obstructionStep = f).build() );
				obstructionFilterCat.addEntry(entryBuilder
						.startFloatField(new TranslatableText("dynamicsoundfilters.config.obstruction.max"),
								data.obstructionFilter.obstructionMax)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.obstruction.max.tooltip") )
						// defaults
						.setDefaultValue(obstruction.getObstructionMax()).setMin(0f).setMax(1f)
						.setSaveConsumer(f -> data.obstructionFilter.obstructionMax = f).build() );
			}

			return builder.build();
		};
	}
}
