package mrtjp.core.block;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;

public abstract class TTileOrient extends InstancedBlockTile {

    public byte orientation = 0;

    public int side() {
        return orientation >> 2;
    }

    public void setSide(int s) {
        byte oldOrient = orientation;
        orientation = (byte) (orientation & 0x3 | s << 2);
        if (oldOrient != orientation) onOrientChanged(oldOrient);
    }

    public int rotation() {
        return orientation & 0x3;
    }

    public void setRotation(int r) {
        byte oldOrient = orientation;
        orientation = (byte) (orientation & 0xfc | r);
        if (oldOrient != orientation) onOrientChanged(oldOrient);
    }

    public BlockCoord position() {
        return new BlockCoord(xCoord, yCoord, zCoord);
    }

    public Transformation rotationT() {
        return Rotation.sideOrientation(side(), rotation()).at(Vector3.center);
    }

    public void onOrientChanged(int oldOrient) {}

    // internal r from absRot
    public int toInternal(int absRot) {
        return  (absRot + 6 - rotation()) % 4;
    }

    // absRot from internal r
    public int toAbsolute(int r) {
        return  (r + rotation() + 2) % 4;
    }

    // absDir from absRot
    public int absoluteDir(int absRot) {
        return Rotation.rotateSide(side(), absRot);
    }

    // absRot from absDir
    public int absoluteRot(int absDir) {
        return Rotation.rotationTo(side(), absDir);
    }
}
