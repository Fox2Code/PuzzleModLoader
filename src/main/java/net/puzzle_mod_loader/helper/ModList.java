package net.puzzle_mod_loader.helper;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public final class ModList implements Iterable<ModInfo> {
    public ModList(ModInfo[] modInfos) {
        this.modInfos = modInfos;
    }

    private final ModInfo[] modInfos;

    public final ModInfo getForID(String id) {
        for (ModInfo modInfo:modInfos) {
            if (modInfo.id.equals(id)) {
                return modInfo;
            }
        }
        return null;
    }

    public final ModInfo getForDisplay(String display) {
        for (ModInfo modInfo:modInfos) {
            if (modInfo.display.equals(display)) {
                return modInfo;
            }
        }
        return null;
    }

    public final boolean hasHash(String hash) {
        for (ModInfo modInfo:modInfos) {
            if (modInfo.hash.equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public final ModInfo[] toArray() {
        return Arrays.copyOf(modInfos, modInfos.length);
    }

    public final int length() {
        return modInfos.length;
    }

    @NotNull
    @Override
    public final Iterator<ModInfo> iterator() {
        return new Iterator<ModInfo>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < modInfos.length;
            }

            @Override
            public ModInfo next() {
                return modInfos[index++];
            }
        };
    }

    ///////////////
    // I/O UTILS //
    ///////////////

    public static ModList decode(byte[] bytes) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        dataInputStream.skip(4);
        return decodeRaw(dataInputStream);
    }

    public static ModList decode(ByteBuf byteBuffer) throws IOException {
        byte[] bytes = new byte[byteBuffer.readInt()];
        byteBuffer.readBytes(bytes);
        return decodeRaw(new DataInputStream(new ByteArrayInputStream(bytes)));
    }

    public static ModList decodeRaw(DataInput data) throws IOException {
        ModInfo[] modInfos = new ModInfo[data.readInt()];
        for (int i = 0; i < modInfos.length;i++) {
            modInfos[i] = new ModInfo(data.readUTF(), data.readUTF(), data.readUTF(), data.readUTF(), data.readUTF(), data.readUTF());
        }
        return new ModList(modInfos);
    }

    public static void encode(ByteBuf byteBuffer, ModList modList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        encodeRaw(new DataOutputStream(byteArrayOutputStream), modList);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteBuffer.writeInt(bytes.length);
        byteBuffer.writeBytes(bytes);
    }

    public static void encodeRaw(DataOutput data, ModList modList) throws IOException {
        data.writeInt(modList.length());
        for (ModInfo modInfo:modList) {
            data.writeUTF(modInfo.id);
            data.writeUTF(modInfo.display);
            data.writeUTF(modInfo.version);
            data.writeUTF(modInfo.hash);
            data.writeUTF(modInfo.signature);
            data.writeUTF(modInfo.flags);
        }
    }
}
