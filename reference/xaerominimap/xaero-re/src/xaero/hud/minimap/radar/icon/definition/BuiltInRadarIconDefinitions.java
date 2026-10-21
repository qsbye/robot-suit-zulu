/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ambient.BatModel;
import net.minecraft.client.model.animal.armadillo.ArmadilloModel;
import net.minecraft.client.model.animal.axolotl.AdultAxolotlModel;
import net.minecraft.client.model.animal.axolotl.BabyAxolotlModel;
import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.chicken.BabyChickenModel;
import net.minecraft.client.model.animal.dolphin.DolphinModel;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.animal.fish.PufferfishBigModel;
import net.minecraft.client.model.animal.fish.PufferfishMidModel;
import net.minecraft.client.model.animal.fish.PufferfishSmallModel;
import net.minecraft.client.model.animal.fish.SalmonModel;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.animal.fish.TropicalFishSmallModel;
import net.minecraft.client.model.animal.frog.FrogModel;
import net.minecraft.client.model.animal.frog.TadpoleModel;
import net.minecraft.client.model.animal.ghast.HappyGhastModel;
import net.minecraft.client.model.animal.goat.GoatModel;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.animal.panda.PandaModel;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.animal.rabbit.RabbitModel;
import net.minecraft.client.model.animal.sniffer.SnifferModel;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.blaze.BlazeModel;
import net.minecraft.client.model.monster.breeze.BreezeModel;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.model.monster.endermite.EndermiteModel;
import net.minecraft.client.model.monster.ghast.GhastModel;
import net.minecraft.client.model.monster.guardian.GuardianModel;
import net.minecraft.client.model.monster.hoglin.HoglinModel;
import net.minecraft.client.model.monster.phantom.PhantomModel;
import net.minecraft.client.model.monster.ravager.RavagerModel;
import net.minecraft.client.model.monster.shulker.ShulkerModel;
import net.minecraft.client.model.monster.silverfish.SilverfishModel;
import net.minecraft.client.model.monster.slime.MagmaCubeModel;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.model.monster.strider.StriderModel;
import net.minecraft.client.model.monster.warden.WardenModel;
import net.minecraft.client.model.monster.wither.WitherBossModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3fc;
import xaero.common.minimap.render.radar.EntityIconDefinitions;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.radar.icon.cache.id.variant.CushionVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.EndermanVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.HorseVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.IronGolemVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.LlamaVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.SaddleVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.TamableVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.TropicalFishVariant;
import xaero.hud.minimap.radar.icon.cache.id.variant.VillagerVariant;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RadarIconCustomPrerenderer;

public class BuiltInRadarIconDefinitions {
    public static final Method BUILD_VARIANT_ID_STRING_METHOD;
    public static final Method GET_VARIANT_ID_STRING_METHOD;

    public static List<String> getMainModelPartFields(EntityRenderer<?, ?> renderer, EntityModel<?> model, Entity entity) {
        ArrayList<String> result = new ArrayList<String>();
        if (model instanceof BatModel) {
            String modelClassPath = BatModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3321"));
            result.add(String.format("%s;%s", modelClassPath, "f_102184_"));
        } else if (model instanceof BlazeModel) {
            String modelClassPath = BlazeModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3329"));
            result.add(String.format("%s;%s", modelClassPath, "f_102245_"));
        } else if (model instanceof SpiderModel) {
            String modelClassPath = SpiderModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3583"));
            result.add(String.format("%s;%s", modelClassPath, "f_103852_"));
        } else if (model instanceof CreeperModel) {
            String modelClassPath = CreeperModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3360"));
            result.add(String.format("%s;%s", modelClassPath, "f_102451_"));
        } else if (model instanceof LlamaModel) {
            String modelClassPath = LlamaModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_27443"));
            result.add(String.format("%s;%s", modelClassPath, "f_103031_"));
        } else if (model instanceof ParrotModel) {
            String modelClassPath = ParrotModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3452"));
            result.add(String.format("%s;%s", modelClassPath, "f_103188_"));
        } else if (model instanceof RabbitModel) {
            String modelClassPath = RabbitModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_27486"));
            result.add(String.format("%s;%s", modelClassPath, "f_103523_"));
        } else if (model instanceof RavagerModel) {
            String modelClassPath = RavagerModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3386"));
            result.add(String.format("%s;%s", modelClassPath, "f_103598_"));
        } else if (model instanceof IronGolemModel) {
            String modelClassPath = IronGolemModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3415"));
            result.add(String.format("%s;%s", modelClassPath, "f_102936_"));
        } else if (model instanceof SnowGolemModel) {
            String modelClassPath = SnowGolemModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3568"));
            result.add(String.format("%s;%s", modelClassPath, "f_103839_"));
        } else if (model instanceof EnderDragonModel) {
            String modelClassPath = EnderDragonModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3630"));
            result.add(String.format("%s;%s", modelClassPath, "f_114235_"));
        } else if (model instanceof ShulkerModel) {
            String modelClassPath = ShulkerModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_3554"));
            result.add(String.format("%s;%s", modelClassPath, "f_103724_"));
        } else if (model instanceof SlimeModel || model instanceof SulfurCubeModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "cube")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("field_3661['%s']", "cube")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("f_104213_['%s']", "cube")));
        } else if (model instanceof AdultAxolotlModel) {
            String modelClassPath = AdultAxolotlModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_28379"));
            result.add(String.format("%s;%s", modelClassPath, "f_170365_"));
        } else if (model instanceof MagmaCubeModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), "children['inside_cube']"));
            result.add(String.format("%s;%s", ModelPart.class.getName(), "field_3661['inside_cube']"));
            result.add(String.format("%s;%s", ModelPart.class.getName(), "f_104213_['inside_cube']"));
        } else if (model instanceof SquidModel || model instanceof GhastModel || model instanceof HappyGhastModel || model instanceof StriderModel || model instanceof PhantomModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "body")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("field_3661['%s']", "body")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("f_104213_['%s']", "body")));
        } else if (model instanceof WardenModel || model instanceof FrogModel || model instanceof BabyAxolotlModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "head")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("field_3661['%s']", "head")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("f_104213_['%s']", "head")));
        } else if (model instanceof SnifferModel) {
            String modelClassPath = SnifferModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_43085"));
            result.add(String.format("%s;%s", modelClassPath, "f_273862_"));
        } else if (model instanceof CamelModel) {
            String modelClassPath = CamelModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_40464"));
            result.add(String.format("%s;%s", modelClassPath, "f_243837_"));
        } else if (model instanceof BreezeModel) {
            String modelClassPath = BreezeModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_47435"));
            result.add(String.format("%s;%s", modelClassPath, "f_302678_"));
        } else if (model instanceof ArmadilloModel) {
            String modelClassPath = ArmadilloModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_47872"));
        } else if (model instanceof CreakingModel) {
            String modelClassPath = CreakingModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_54844"));
        } else if (model instanceof WolfModel) {
            String modelClassPath = WolfModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "head"));
            result.add(String.format("%s;%s", modelClassPath, "field_20788"));
        } else if (model instanceof NautilusModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "shell")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("field_3661['%s']", "shell")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("f_104213_['%s']", "shell")));
        } else if (model instanceof DolphinModel) {
            String modelClassPath = DolphinModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "body"));
        } else if (model instanceof BabyChickenModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "body")));
        }
        return result;
    }

    public static List<String> getSecondaryModelPartsFields(EntityRenderer<?, ?> renderer, EntityModel<?> model, Entity entity) {
        ArrayList<String> result = new ArrayList<String>();
        if (model instanceof RabbitModel) {
            String modelClassPath = RabbitModel.class.getName();
            result.add(String.format("%s;%s", modelClassPath, "rightEar"));
            result.add(String.format("%s;%s", modelClassPath, "field_27487"));
            result.add(String.format("%s;%s", modelClassPath, "f_170877_"));
            result.add(String.format("%s;%s", modelClassPath, "leftEar"));
            result.add(String.format("%s;%s", modelClassPath, "field_27488"));
            result.add(String.format("%s;%s", modelClassPath, "f_170878_"));
            result.add(String.format("%s;%s", modelClassPath, "nose"));
            result.add(String.format("%s;%s", modelClassPath, "field_3530"));
            result.add(String.format("%s;%s", modelClassPath, "f_103527_"));
        } else if (model instanceof NautilusModel) {
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("children['%s']", "body")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("field_3661['%s']", "body")));
            result.add(String.format("%s;%s", ModelPart.class.getName(), String.format("f_104213_['%s']", "body")));
        }
        return result;
    }

    public static Object getModelRoot(Model entityModel) {
        if (entityModel instanceof SquidModel || entityModel instanceof GhastModel || entityModel instanceof HappyGhastModel || entityModel instanceof SlimeModel || entityModel instanceof SulfurCubeModel || entityModel instanceof PhantomModel || entityModel instanceof StriderModel || entityModel instanceof MagmaCubeModel || entityModel instanceof BabyChickenModel) {
            return entityModel.root();
        }
        if (entityModel instanceof NautilusModel) {
            return entityModel.root().getChild("root");
        }
        if (entityModel instanceof WardenModel) {
            return entityModel.root().getChild("bone").getChild("body");
        }
        if (entityModel instanceof FrogModel) {
            return entityModel.root().getChild("body");
        }
        if (entityModel instanceof BabyAxolotlModel) {
            return entityModel.root().getChild("root").getChild("body");
        }
        return entityModel;
    }

    public static boolean forceFieldCheck(EntityModel<?> entityModel) {
        return entityModel instanceof AdultAxolotlModel || entityModel instanceof WolfModel || entityModel instanceof DolphinModel;
    }

    public static void prepareEntityRenderState(EntityRenderState renderState) {
        if (renderState instanceof SlimeRenderState) {
            SlimeRenderState cubeState = (SlimeRenderState)renderState;
            cubeState.squish = 0.0f;
            if (renderState instanceof SulfurCubeRenderState) {
                SulfurCubeRenderState sulfurState = (SulfurCubeRenderState)renderState;
                sulfurState.containedBlock.clear();
            }
        }
    }

    public static void defaultTransformation(PoseStack matrixStack, EntityModel em) {
        if (em instanceof CodModel || em instanceof SalmonModel) {
            OptimizedMath.rotatePose(matrixStack, 90.0f, (Vector3fc)OptimizedMath.YP);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof TropicalFishLargeModel || em instanceof TropicalFishSmallModel) {
            OptimizedMath.rotatePose(matrixStack, 90.0f, (Vector3fc)OptimizedMath.YP);
        } else if (em instanceof BatModel) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof AbstractEquineModel) {
            OptimizedMath.rotatePose(matrixStack, 65.0f, (Vector3fc)OptimizedMath.XP);
            matrixStack.scale(0.7f, 0.7f, 0.7f);
        } else if (em instanceof DolphinModel || em instanceof GoatModel) {
            matrixStack.scale(0.7f, 0.7f, 0.7f);
        } else if (em instanceof GuardianModel || em instanceof SquidModel) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof HoglinModel) {
            OptimizedMath.rotatePose(matrixStack, 45.0f, (Vector3fc)OptimizedMath.XP);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof LlamaModel || em instanceof CamelModel || em instanceof SnifferModel) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof GhastModel || em instanceof HappyGhastModel || em instanceof RavagerModel || em instanceof StriderModel || em instanceof EnderDragonModel || em instanceof SulfurCubeModel) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else if (em instanceof WitherBossModel) {
            matrixStack.scale(0.35f, 0.35f, 0.35f);
        } else if (em instanceof PhantomModel) {
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            OptimizedMath.rotatePose(matrixStack, 90.0f, (Vector3fc)OptimizedMath.XP);
        } else if (em instanceof PandaModel) {
            matrixStack.scale(0.7f, 0.7f, 0.7f);
        } else if (em instanceof WardenModel) {
            matrixStack.scale(0.7f, 0.7f, 0.7f);
        } else if (em instanceof NautilusModel) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            OptimizedMath.rotatePose(matrixStack, 90.0f, (Vector3fc)OptimizedMath.YP);
        }
    }

    public static void defaultPostIconModelRender(PoseStack matrixStack, EntityModel entityModel) {
    }

    public static boolean fullModelIcon(EntityModel em) {
        return em instanceof CodModel || em instanceof SalmonModel || em instanceof TropicalFishLargeModel || em instanceof TropicalFishSmallModel || em instanceof BeeModel || em instanceof GuardianModel || em instanceof EndermiteModel || em instanceof MagmaCubeModel || em instanceof SlimeModel || em instanceof SulfurCubeModel || em instanceof PufferfishBigModel || em instanceof PufferfishMidModel || em instanceof PufferfishSmallModel || em instanceof SilverfishModel || em instanceof WitherBossModel || em instanceof TadpoleModel;
    }

    public static <S extends EntityRenderState> RadarIconCustomPrerenderer getCustomLayer(EntityRenderer<?, ? super S> entityRenderer, Entity entity) {
        return null;
    }

    public static <E extends Entity> Object getVariant(Identifier entityTexture, EntityRenderer<? super E, ?> entityRenderer, E entity) {
        if (entity instanceof Horse) {
            return new HorseVariant(entityTexture, ((Horse)entity).getMarkings());
        }
        if (entity instanceof VillagerDataHolder) {
            VillagerData villagerdata = ((VillagerDataHolder)entity).getVillagerData();
            VillagerType villagertype = (VillagerType)villagerdata.type().value();
            Identifier villagerTypeKey = BuiltInRegistries.VILLAGER_TYPE.getKey((Object)villagertype);
            VillagerProfession villagerprofession = (VillagerProfession)villagerdata.profession().value();
            Identifier villagerProfessionKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey((Object)villagerprofession);
            int villagerprofessionlevel = villagerdata.level();
            return new VillagerVariant(entityTexture, ((LivingEntity)entity).isBaby(), villagerTypeKey, villagerProfessionKey, villagerprofessionlevel);
        }
        if (entity instanceof Cat || entity instanceof Wolf) {
            return new TamableVariant(entityTexture, ((TamableAnimal)entity).isTame());
        }
        if (entity instanceof IronGolem) {
            return new IronGolemVariant(entityTexture, ((IronGolem)entity).getCrackiness());
        }
        if (entity instanceof Llama) {
            Llama llama = (Llama)entity;
            return new LlamaVariant(entityTexture, llama.isTraderLlama(), llama.getBodyArmorItem().getItem());
        }
        if (entity instanceof Pig) {
            return new SaddleVariant(entityTexture, ((Pig)entity).isSaddled());
        }
        if (entity instanceof Strider) {
            return new SaddleVariant(entityTexture, ((Strider)entity).isSaddled());
        }
        if (entity instanceof TropicalFish) {
            TropicalFish fish = (TropicalFish)entity;
            return new TropicalFishVariant(entityTexture, fish.getPattern(), fish.getBaseColor(), fish.getPatternColor());
        }
        if (entity instanceof Enderman) {
            Enderman enderman = (Enderman)entity;
            return new EndermanVariant(entityTexture, enderman.isCreepy());
        }
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            return BuiltInRegistries.ITEM.getKey((Object)itemEntity.getItem().getItem());
        }
        if (entity instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame)entity;
            ItemStack itemFrameStack = itemFrame.getItem();
            Item item = itemFrameStack == null ? Items.AIR : itemFrameStack.getItem();
            return BuiltInRegistries.ITEM.getKey((Object)item);
        }
        if (entity instanceof Cushion) {
            Cushion cushion = (Cushion)entity;
            DyeColor cushionColor = cushion.getColor();
            return new CushionVariant(cushionColor);
        }
        return entityTexture == null ? "default" : entityTexture;
    }

    static {
        try {
            BUILD_VARIANT_ID_STRING_METHOD = EntityIconDefinitions.class.getDeclaredMethod("buildVariantIdString", StringBuilder.class, EntityRenderer.class, Entity.class);
            GET_VARIANT_ID_STRING_METHOD = EntityIconDefinitions.class.getDeclaredMethod("getVariantString", EntityRenderer.class, Entity.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

