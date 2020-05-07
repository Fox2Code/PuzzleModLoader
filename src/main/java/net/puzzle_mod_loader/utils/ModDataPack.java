package net.puzzle_mod_loader.utils;

import net.puzzle_mod_loader.core.Mod;
import net.puzzle_mod_loader.core.ModLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class ModDataPack extends FileResourcePack {
    private HashMap<String, byte[]> extData;
    private Set<String> assetsNameSpace;
    private Set<String> dataNameSpace;
    private Set<String> warned;
    private String metaFallback;
    private Mod mod;

    public ModDataPack(Mod mod) {
        super(mod.getFile());
        if (mod.getDataPack() != null) {
            throw new IllegalStateException("ModDataPack already init for "+mod.getName());
        }
        this.extData = new HashMap<>();
        this.warned = new HashSet<>();
        this.assetsNameSpace = this.getNamespaces(PackType.CLIENT_RESOURCES);
        if (this.assetsNameSpace == null) this.assetsNameSpace = new HashSet<>();
        this.dataNameSpace = this.getNamespaces(PackType.SERVER_DATA);
        if (this.dataNameSpace == null) this.dataNameSpace = new HashSet<>();
        this.metaFallback = "{\"pack\": {\"pack_format\": 5,\n\"description\": \""+mod.getName()+" v"+mod.getVersion()+"\"}}";
        this.mod = mod;
    }

    @Override
    public InputStream getResource(String s) throws IOException {
        byte[] data = extData.get(s);
        if (data != null) {
            return new ByteArrayInputStream(data);
        }
        if (s.equals("pack.mcmeta")) {
            try {
                return super.getResource(s);
            } catch (ResourcePackFileNotFoundException e) {
                return new ByteArrayInputStream(metaFallback.getBytes(StandardCharsets.UTF_8));
            }
        }
        try {
            return super.getResource(s);
        } catch (FileNotFoundException e) {
            if (s.startsWith("assets/"+this.mod.getId()+"/") ||
                    s.startsWith("data/"+this.mod.getId()+"/") && this.warned.add(s)) {
                ModLoader.LOGGER.warn("Mod "+this.mod.getName()+" should init "+s+" !");
            }
            throw e;
        }
    }

    @Override
    public boolean hasResource(String s) {
        return s.equals("pack.mcmeta") || extData.containsKey(s) || super.hasResource(s);
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        switch (packType) {
            case CLIENT_RESOURCES:
                return this.assetsNameSpace;
            case SERVER_DATA:
                return this.dataNameSpace;
        }
        throw new UnsupportedOperationException("Unknown PackType: "+packType.name());
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5) {
        Collection<ResourceLocation> collection = super.getResources(var1, var2, var3, var4, var5);
        String var9 = var1.getDirectory() + "/" + var2 + "/";
        String var10 = var9 + var3;
        for (String key:extData.keySet()) {
            if (!key.endsWith(".mcmeta") && key.startsWith(var10)) {
                String var13 = key.substring(var9.length());
                String[] var14 = var13.split("/");
                if (var14.length >= var4 + 1 && var5.test(var14[var14.length - 1])) {
                    collection.add(new ResourceLocation(var2, var13));
                }
            }
        }
        Collections.sort((List<ResourceLocation>) collection);
        System.out.println(var10 + " => "+collection.toString());
        return collection;
    }

    public void injectResource(String path, String data) {
        this.injectResource(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public void injectResource(String path, byte[] data) {
        if (data == null) data = new byte[0];
        if (path.startsWith("assets/")) {
            String tmp = path.substring(7);
            tmp = tmp.substring(0, tmp.indexOf('/'));
            assetsNameSpace.add(tmp);
        } else if (path.startsWith("data/")) {
            String tmp = path.substring(5);
            tmp = tmp.substring(0, tmp.indexOf('/'));
            dataNameSpace.add(tmp);
        }
        extData.put(path, data);
    }

    @Override
    public String getName() {
        return "mod/"+this.mod.getId();
    }
}
