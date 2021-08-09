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
import java.util.Arrays;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.TranslatableText;

public class ConfigScreen implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		ConfigData data = Config.getData();
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
							.setDefaultValue(0.8f).setMin(0.0f).setMax(1.0f)
							.setSaveConsumer(f -> data.liquidFilter.waterGain = f).build() );
					waterCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf"),
									data.liquidFilter.waterGainHF)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf.tooltip") )
							.setDefaultValue(0.3f).setMin(0.0f).setMax(1.0f)
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
							.setDefaultValue(0.5f).setMin(0.0f).setMax(1.0f)
							.setSaveConsumer(f -> data.liquidFilter.lavaGain = f).build() );
					lavaCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf"),
									data.liquidFilter.lavaGainHF)
							.setTooltip(new TranslatableText("dynamicsoundfilters.config.liquid.gainhf.tooltip") )
							.setDefaultValue(0.1f).setMin(0.0f).setMax(1.0f)
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
						.setDefaultValue(1.0f).setMin(0.0f).setMax(2.0f)
						.setSaveConsumer(f -> data.reverbFilter.reverbPercent = f).build() );

				reverbFilterCat.addEntry(entryBuilder
						.startStrList(new TranslatableText("dynamicsoundfilters.config.reverb.dimensions"),
								data.reverbFilter.dimensionBaseReverb)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip1"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip2"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip3"),
								new TranslatableText("dynamicsoundfilters.config.reverb.dimensions.tooltip4") )
						.setDefaultValue(Arrays.asList("minecraft:the_nether;1.0") ).setSaveConsumer(l -> {
							data.reverbFilter.dimensionBaseReverb = l;
							data.reverbFilter.recalculateCache();
						}).build() );
				reverbFilterCat.addEntry(entryBuilder
						.startStrList(new TranslatableText("dynamicsoundfilters.config.reverb.blocks"),
								data.reverbFilter.customBlockReverb)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip1"),
								new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip2"),
								new TranslatableText("dynamicsoundfilters.config.reverb.blocks.tooltip3") )
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
							.setDefaultValue(1024).setMin(0).setMax(Integer.MAX_VALUE)
							.setSaveConsumer(i -> data.reverbFilter.maxBlocks = i).build() );

					scannerCat.add(entryBuilder
							.startBooleanToggle(
									new TranslatableText("dynamicsoundfilters.config.reverb.scanner.checksky"),
									data.reverbFilter.checkSky)
							.setTooltip(
									new TranslatableText("dynamicsoundfilters.config.reverb.scanner.checksky.tooltip") )
							.setDefaultValue(true).setSaveConsumer(b -> data.reverbFilter.checkSky = b)
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
							.setDefaultValue(0.2f).setMin(0.0f).setMax(1.0f)
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
							.setDefaultValue(0.15f).setMin(0.0f).setMax(1.0f)
							.setSaveConsumer(f -> data.reverbFilter.gain = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(new TranslatableText("dynamicsoundfilters.config.reverb.advanced.gainhf"),
									data.reverbFilter.gainHF)
							.setDefaultValue(0.8f).setMin(0.0f).setMax(1.0f)
							.setSaveConsumer(f -> data.reverbFilter.gainHF = f).build() );
					advancedCat
							.add(entryBuilder
									.startFloatField(
											new TranslatableText(
													"dynamicsoundfilters.config.reverb.advanced.mindecaytime"),
											data.reverbFilter.minDecayTime)
									.setDefaultValue(0.1f).setMin(0.1f).setMax(20.0f)
									.setSaveConsumer(f -> data.reverbFilter.minDecayTime = f).build() );
					advancedCat
							.add(entryBuilder
									.startFloatField(
											new TranslatableText(
													"dynamicsoundfilters.config.reverb.advanced.decayhfratio"),
											data.reverbFilter.decayHFRatio)
									.setDefaultValue(0.7f).setMin(0.1f).setMax(20.0f)
									.setSaveConsumer(f -> data.reverbFilter.decayHFRatio = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.airabsorptiongainhf"),
									data.reverbFilter.airAbsorptionGainHF)
							.setDefaultValue(0.99f).setMin(0.892f).setMax(1.0f)
							.setSaveConsumer(f -> data.reverbFilter.airAbsorptionGainHF = f).build() );

					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsgainbase"),
									data.reverbFilter.reflectionsGainBase)
							.setDefaultValue(0.05f).setMin(0.0f).setMax(1.58f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsGainBase = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsgainmultiplier"),
									data.reverbFilter.reflectionsGainMultiplier)
							.setDefaultValue(0.05f).setMin(0.0f).setMax(1.58f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsGainMultiplier = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.reflectionsdelaymultiplier"),
									data.reverbFilter.reflectionsDelayMultiplier)
							.setDefaultValue(0.025f).setMin(0.0f).setMax(0.3f)
							.setSaveConsumer(f -> data.reverbFilter.reflectionsDelayMultiplier = f)
							.build() );

					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbgainbase"),
									data.reverbFilter.lateReverbGainBase)
							.setDefaultValue(1.26f).setMin(0.0f).setMax(5.0f)
							.setSaveConsumer(f -> data.reverbFilter.lateReverbGainBase = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbgainmultiplier"),
									data.reverbFilter.lateReverbGainMultiplier)
							.setDefaultValue(0.1f).setMin(0.0f).setMax(5.0f)
							.setSaveConsumer(f -> data.reverbFilter.lateReverbGainMultiplier = f).build() );
					advancedCat.add(entryBuilder
							.startFloatField(
									new TranslatableText(
											"dynamicsoundfilters.config.reverb.advanced.latereverbdelaymultiplier"),
									data.reverbFilter.lateReverbDelayMultiplier)
							.setDefaultValue(0.01f).setMin(0.0f).setMax(0.1f)
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
						.setDefaultValue(true).setSaveConsumer(b -> data.obstructionFilter.enabled = b)
						.build() );

				obstructionFilterCat.addEntry(entryBuilder
						.startFloatField(new TranslatableText("dynamicsoundfilters.config.obstruction.step"),
								data.obstructionFilter.obstructionStep)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.obstruction.step.tooltip") )
						.setDefaultValue(0.1f).setMin(0.0f).setMax(1.0f)
						.setSaveConsumer(f -> data.obstructionFilter.obstructionStep = f).build() );
				obstructionFilterCat.addEntry(entryBuilder
						.startFloatField(new TranslatableText("dynamicsoundfilters.config.obstruction.max"),
								data.obstructionFilter.obstructionMax)
						.setTooltip(new TranslatableText("dynamicsoundfilters.config.obstruction.max.tooltip") )
						.setDefaultValue(0.98f).setMin(0.0f).setMax(1.0f)
						.setSaveConsumer(f -> data.obstructionFilter.obstructionMax = f).build() );
			}

			return builder.build();
		};
	}
}
