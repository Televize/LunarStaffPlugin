package club.bigpp.lunar;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

@Getter
class Mod {
    private String id;
    private byte[] enabled;
    private byte[] disabled;

    Mod(String id) {
        this.id = id;
        this.enabled = this.createPacket(id, true);
        this.disabled = this.createPacket(id, false);
    }

    private byte[] createPacket(String id, boolean state) {
        ByteBuf buf = Unpooled.buffer();

        /*
            Write Packet ID
         */
        buf.writeByte(12);

        /*
            Write Mod ID (https://wiki.vg/Protocol#VarInt_and_VarLong)
         */
        byte[] b = id.getBytes();
        int length = b.length;

        while ((length & -128) != 0) {
            buf.writeByte(length & 127 | 128);
            length >>>= 7;
        }
        buf.writeByte(length);
        buf.writeBytes(b);

        /*
            Write Mod State
         */
        buf.writeBoolean(state);

        return buf.array();
    }
}
