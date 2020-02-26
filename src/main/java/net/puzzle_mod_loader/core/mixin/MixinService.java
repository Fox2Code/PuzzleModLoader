package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.launch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class MixinService extends MixinServiceAbstract implements IMixinService,IClassProvider,IClassBytecodeProvider,ITransformerProvider,IClassTracker {
    private final ContainerHandleVirtual containerHandleVirtual = new ContainerHandleVirtual("puzzle");

    @Override
    public String getName() {
        return "PuzzleMixinService";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.emptyList();
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return containerHandleVirtual;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Launch.getClassLoader().getResourceAsStream(name);
    }

    @Override
    public URL[] getClassPath() {
        return Launch.getClassLoader().getURLs();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Launch.getClassLoader().loadClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.getClassLoader());
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.getClassLoader());
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, false);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        ClassNode classNode = new ClassNode();
        InputStream is = Launch.getClassLoader().getResourceAsStream(name + ".class");
        if (is == null) throw new ClassNotFoundException(name);
        new ClassReader(is).accept(classNode, 0);
        Launch.compactTransformer.patchClassNode(classNode);
        return classNode;
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        return null;
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return null;
    }

    @Override
    public void addTransformerExclusion(String name) {
        Launch.getClassLoader().addTransformerExclusion(name);
    }

    @Override
    public void registerInvalidClass(String className) {
        //Invalid classes are not implemented in this context
    }

    @Override
    public boolean isClassLoaded(String className) {
        return Launch.getClassLoader().isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return MixinEnvironment.Phase.INIT;
    }
}
