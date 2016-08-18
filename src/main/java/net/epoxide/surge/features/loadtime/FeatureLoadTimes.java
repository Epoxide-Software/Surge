package net.epoxide.surge.features.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.mappings.ClassMapping;
import net.epoxide.surge.asm.mappings.MethodMapping;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.libs.Constants;
import net.epoxide.surge.libs.TextUtils;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;

/**
 * Tracks the load time for mods, at various load stages. While these load times are not 100%
 * accurate, they do provide a glimpse into what is going on.
 */
public class FeatureLoadTimes extends Feature {
    
    public ClassMapping CLASS_LOAD_CONTROLLER = new ClassMapping("net.minecraftforge.fml.common.LoadController");
    public MethodMapping METHOD_SEND_EVENT_TO_MOD_CONTAINER = new MethodMapping("sendEventToModContainer", void.class, FMLEvent.class, ModContainer.class);
    
    /**
     * A map that holds the load time of all mods, at various stages.
     */
    private static final HashMap<String, List<LoadTime>> LOAD_TIMES = new HashMap<>();
    
    /**
     * A map which holds the combined load time of every stage.
     */
    private static final HashMap<String, Long> LOAD_TOTAL_TIME = new HashMap<>();
    
    /**
     * Format to use when representing the current data and time.
     */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    
    @Override
    public void onFMLFinished () {
        
        final File surgeDirectory = new File("surge");
        final String timestamp = TIME_FORMAT.format(new Date());
        
        if (!surgeDirectory.exists())
            surgeDirectory.mkdirs();
        
        final File loadDirectory = new File(surgeDirectory, "loadtimes");
        
        if (!loadDirectory.exists())
            loadDirectory.mkdirs();
            
        try (FileWriter writer = new FileWriter(new File(loadDirectory, "Surge-Load-Time-Analysis-" + timestamp + ".txt"))) {
            
            writer.write("#Surge Load Time Analysis - " + timestamp + SystemUtils.LINE_SEPARATOR);
            
            for (final String line : TextUtils.wrapStringToList("This file contains approximate information about how long each mod takes to load. The load time of each mod is split into groups which represent the loading stages of the game. If a mod does not have a load time listed, it took less than 0.01 seconds to load. Please note that a mod being on this list does not mean it is slow or broken. While this can be the case, load times can vary depending on how much content a mod provides.", 80, false, new ArrayList<String>()))
                writer.write(line + SystemUtils.LINE_SEPARATOR);
                
            writer.write(SystemUtils.LINE_SEPARATOR);
            
            long totalTime = 0;
            
            for (final String key : LOAD_TOTAL_TIME.keySet())
                totalTime += LOAD_TOTAL_TIME.get(key);
                
            writer.write(String.format("Total time: %.2f sec", totalTime / 1000d) + SystemUtils.LINE_SEPARATOR);
            
            writer.write(SystemUtils.LINE_SEPARATOR);
            
            for (final String key : LOAD_TIMES.keySet()) {
                
                writer.write(String.format("#%s - %.2f sec", key, LOAD_TOTAL_TIME.get(key) / 1000d) + SystemUtils.LINE_SEPARATOR);
                
                final List<LoadTime> times = LOAD_TIMES.get(key);
                times.sort( (a, b) -> a.getTime() < b.getTime() ? 1 : a.getTime() == b.getTime() ? 0 : -1);
                
                for (final LoadTime time : times)
                    writer.write(time.toString() + SystemUtils.LINE_SEPARATOR);
                    
                writer.write(SystemUtils.LINE_SEPARATOR);
            }
        }
        catch (final IOException exception) {
            
            Constants.LOG.warn(exception);
        }
    }
    
    /**
     * Stores the loading time of a mod.
     *
     * @param mod The mod being loaded.
     * @param stateEvent The event being tracked.
     * @param startTime The time the event started.
     * @param endTime The time the event ended.
     */
    public static void registerLoadingTime (ModContainer mod, FMLEvent stateEvent, long startTime, long endTime) {
        
        final String stageName = stateEvent.getClass().getSimpleName();
        final long elapsed = endTime - startTime;
        
        if (elapsed < 10)
            return;
            
        final LoadTime loadTime = new LoadTime(mod.getName(), elapsed);
        
        final Long totalTime = LOAD_TOTAL_TIME.get(stageName);
        
        if (totalTime == null)
            LOAD_TOTAL_TIME.put(stageName, elapsed);
            
        else
            LOAD_TOTAL_TIME.put(stageName, totalTime + elapsed);
            
        if (LOAD_TIMES.containsKey(stageName))
            LOAD_TIMES.get(stageName).add(loadTime);
            
        else {
            
            final List<LoadTime> times = new ArrayList<>();
            times.add(loadTime);
            LOAD_TIMES.put(stageName, times);
        }
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        final MethodNode method = this.METHOD_SEND_EVENT_TO_MOD_CONTAINER.getMethodNode(clazz);
        this.transformSendEventToModContainer(method);
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    
    private void transformSendEventToModContainer (MethodNode method) {
        
        {
            final InsnList needle = new InsnList();
            needle.add(new LdcInsnNode("Sending event %s to mod %s"));
            needle.add(new InsnNode(Opcodes.ICONST_2));
            needle.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
            needle.add(new InsnNode(Opcodes.DUP));
            needle.add(new InsnNode(Opcodes.ICONST_0));
            needle.add(new VarInsnNode(Opcodes.ALOAD, 1));
            needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/event/FMLEvent", "getEventType", "()Ljava/lang/String;", false));
            needle.add(new InsnNode(Opcodes.AASTORE));
            needle.add(new InsnNode(Opcodes.DUP));
            needle.add(new InsnNode(Opcodes.ICONST_1));
            needle.add(new VarInsnNode(Opcodes.ALOAD, 3));
            needle.add(new InsnNode(Opcodes.AASTORE));
            needle.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/FMLLog", "log", "(Ljava/lang/String;Lorg/apache/logging/log4j/Level;Ljava/lang/String;[Ljava/lang/Object;)V", false));
            needle.add(new LabelNode());
            needle.add(new LineNumberNode(-1, new LabelNode()));
            
            final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
            final InsnList newInstr = new InsnList();
            
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            newInstr.add(new VarInsnNode(Opcodes.LSTORE, 5));
            newInstr.add(new LabelNode());
            
            method.instructions.insert(pointer, newInstr);
        }
        {
            final InsnList needle = new InsnList();
            needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/google/common/eventbus/EventBus", "post", "(Ljava/lang/Object;)V", false));
            
            final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle);
            final InsnList newInstr = new InsnList();
            
            newInstr.add(new LabelNode());
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            newInstr.add(new VarInsnNode(Opcodes.LSTORE, 7));
            newInstr.add(new LabelNode());
            newInstr.add(new VarInsnNode(Opcodes.ALOAD, 2));
            newInstr.add(new VarInsnNode(Opcodes.ALOAD, 1));
            newInstr.add(new VarInsnNode(Opcodes.LLOAD, 5));
            newInstr.add(new VarInsnNode(Opcodes.LLOAD, 7));
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/loadtime/FeatureLoadTimes", "registerLoadingTime", "(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/common/event/FMLEvent;JJ)V", false));
            newInstr.add(new LabelNode());
            
            method.instructions.insert(pointer, newInstr);
        }
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return this.CLASS_LOAD_CONTROLLER.isEqual(name);
    }
    
    @Override
    public boolean enabledByDefault () {
        
        return false;
    }
}
