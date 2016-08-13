package net.epoxide.surge.asm;

public class Mappings {
    
    public static final String CLASS_TEXTURE_ATLAS_SPRITE = "net/minecraft/client/renderer/texture/TextureAtlasSprite";
    public static final String CLASS_IRESOURCE = "net/minecraft/client/resources/IResource";
    public static final String CLASS_LOAD_CONTROLLER = "net/minecraftforge/fml/common/LoadController";
    
    public static final Mapping METHOD_UPDATE_ANIMATION = new Mapping("updateAnimation", "()V", CLASS_TEXTURE_ATLAS_SPRITE, false);
    public static final Mapping METHOD_RENDER_CLOUDS = new Mapping("renderClouds", "(FI)V", "net/minecraft/client/renderer/RenderGlobal", false);
    public static final Mapping METHOD_SEND_EVENT_TO_MOD_CONTAINER = new Mapping("sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V", CLASS_LOAD_CONTROLLER, false);
    
    public static final Mapping FIELD_FRAMES_TEXTURE_DATA = new Mapping("field_110976_a", "framesTextureData", "Ljava/util/List;", CLASS_TEXTURE_ATLAS_SPRITE, false);

}
