package com.jn.langx.util.io.file;

import java.util.Set;

/**
 * 基于 FilePermission 进行封装，进行基于用户、用户组等待判断
 */
public class PosixFilePermissions {
    /**
     * FilePermission mask
     */
    private int permissions;
    private final boolean isOwner;
    private final boolean isInGroup;

    public PosixFilePermissions(int permissions, boolean isOwner, boolean isInGroup) {
        this.permissions = permissions;
        this.isOwner = isOwner;
        this.isInGroup = isInGroup;
    }

    public int getPermissions() {
        return permissions;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isInGroup() {
        return isInGroup;
    }

    public boolean isExecutable() {
        if (isOwner) {
            return FilePermission.USR_X.isIn(permissions);
        }
        if (isInGroup) {
            return FilePermission.GRP_X.isIn(permissions);
        }
        return FilePermission.OTH_X.isIn(permissions);
    }

    public boolean isReadable() {
        if (isOwner) {
            return FilePermission.USR_R.isIn(permissions);
        }
        if (isInGroup) {
            return FilePermission.GRP_R.isIn(permissions);
        }
        return FilePermission.OTH_R.isIn(permissions);
    }


    public boolean isWritable() {
        if (isOwner) {
            return FilePermission.USR_W.isIn(permissions);
        }
        if (isInGroup) {
            return FilePermission.GRP_W.isIn(permissions);
        }
        return FilePermission.OTH_W.isIn(permissions);
    }

    public void addPermission(FilePermission filePermission) {
        this.permissions |= filePermission.getVal();
    }

    public void removePermission(FilePermission filePermission) {
        this.permissions &= ~filePermission.getVal();
    }

    public Set<FilePermission> getPermissionSet() {
        return FilePermission.fromMask(this.permissions);
    }
}
