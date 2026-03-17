package me.brynview.navidrohim.common.objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.helper.CommandFactory;
import me.brynview.navidrohim.common.helper.CommonHelper;
import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.io.UserSharingFile;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.common.syncing.SyncingInformation;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dataclass to hold groups and waypoints from server. This is old code, so I wouldn't mess with it.
 */
public class ServerObject extends LegacyObject implements PossessesIdentifier {

    String name;
    String groupIdentifier;

    public UserSharingFile accessorSharing;
    public SyncingInformation syncing;

    public   boolean dataclass;
    public static ObjectType objectType = ObjectType.GENERIC;

    @Nullable
    private Path currentObjectPath;

    @Nullable
    public final Path globalObjectPath;

    @Nullable
    private final Path normalObjectPath;

    protected final UUID ownerUUID;

    public ServerObject(JsonObject payload, UUID playerUUID, boolean dataclass)
    {
        super(payload);
        this.dataclass = dataclass;

        this.ownerUUID = playerUUID; // Note; if you set ownerUUID before this.syncing is defined, it enables some sort of compatibility for legacy clients. But I've left it as-is to avoid chaos.
        this.syncing = SyncingInformation.getSyncingInfo(this);

        this.name = payload.get("name").getAsString();
        this.accessorSharing = !dataclass ? new UserSharingFile(playerUUID) : null;

        this.globalObjectPath = !dataclass ? Path.of(ObjectType.getPathLocationPrefix(this.getObjectType()) + JMWSServerIO.PathUtils.makeFilename(this.syncing.objectIdentifier, this.ownerUUID, true)) : null;
        this.normalObjectPath = !dataclass ? JMWSServerIO.PathUtils.getObjectFilename(this.syncing.getOwner(), this.syncing.objectIdentifier, getObjectType(), false) : null;
        this.groupIdentifier = payload.get("guid").getAsString();

        if (!dataclass)
        {
            this.currentObjectPath = !syncing.isGlobal() ? normalObjectPath : globalObjectPath;
        }
    }

    public ServerObject(JsonObject payload, UUID playerUUID)
    {
        this(payload, playerUUID, false);
    }

    public static List<Path> getGlobalObjects(ObjectType globalObjectType)
    {
        List<Path> wp = new ArrayList<>();
        for (Path path : JMWSServerIO.getAllObjects(globalObjectType).toList())
        {
            if (path.toString().contains(JMWSServerIO.globalObjPrefix))
            {
                wp.add(path);
            }
        }
        return wp;
    }

    public String getName() { return this.name; }

    public String getGroupIdentifier() { return this.groupIdentifier; } // No usages but may be used elsewhere like with generics not sure

    public String getRawString() { return this.payload.toString();}
    public JsonObject getRawJson() { return this.payload;}

    public ObjectType getObjectType()
    {
        return objectType;
    }

    @Nullable
    public Path getCurrentObjectPath()
    {
        return this.currentObjectPath;
    }

    @Nullable
    public Path getGlobalObjectPath()
    {
        return globalObjectPath;
    }

    @Nullable
    public Path getNormalObjectPath()
    {
        return normalObjectPath;
    }

    @Nullable
    public String getDifferentiator() { return "Object"; }

    public Boolean hasFile()
    {
        return this.getCurrentObjectPath() != null && CommonHelper.fileExists(this.getCurrentObjectPath());
    }

    public void removeObjectFromUser(UUID playerUUID, String objectIdentifier)
    {
        UserSharingFile.removeObjectFromUser(playerUUID, objectIdentifier, getObjectType());
        //Player sharedPlayer = JMWSSpigot.server.getPlayer(playerUUID); // TODO: use api
        //if (sharedPlayer != null)
        {
            if (this.getObjectType() == ObjectType.WAYPOINT)
            {
                // TODO: PACKET
                //Dispatcher.sendToClient(new JMWSActionPayload(CommandFactory.makeDeleteRequestJson(objectIdentifier, true, false)), sharedPlayer);
            } else {
                // TODO: PACKET
                //Dispatcher.sendToClient(new JMWSActionPayload(CommandFactory.makeDeleteGroupRequestJson(this.syncing.objectIdentifier, null, true, true, true, false, false)), sharedPlayer);
            }
        }
    }



    public void makeGlobal()
    {
        File oldNameFile =  new File(this.getCurrentObjectPath().toString());
        File newFileName = new File(this.getGlobalObjectPath().toString());
        oldNameFile.renameTo(newFileName);

        this.currentObjectPath = getGlobalObjectPath();
        this.syncing.setGlobal(true);
    }

    public void removeGlobal()
    {
        File oldNameFile = new File(this.getCurrentObjectPath().toString());
        File newFileName = new File(this.getNormalObjectPath().toString());
        this.currentObjectPath = getNormalObjectPath();

        oldNameFile.renameTo(newFileName);
        this.syncing.setGlobal(false);
    }


    public boolean delete(boolean stopSharing)
    {
        if (this.currentObjectPath != null && !dataclass)
        {
            if (stopSharing) {this.stopSharing();}
            return CommonHelper.deleteFile(this.getCurrentObjectPath());
        }
        return false;
    }

    public static boolean deleteAll(UUID user, ObjectType deletionType)
    {
        List<Boolean> deletionStatusList = new ArrayList<>();

        for (Path waypointPath : JMWSServerIO.getObjectPathsForUser(user, deletionType)) {
            deletionStatusList.add(JMWSServerIO.getObjectFromFile(waypointPath, user, deletionType).delete(true));
        }

        return deletionStatusList.isEmpty() || deletionStatusList.stream().allMatch(deletionStatusList.getFirst()::equals);
    }

    public void update(String data, boolean updateSyncInfo)
    {
        if (!updateSyncInfo) {
            ServerObject newChange = new ServerObject(JsonParser.parseString(data).getAsJsonObject(), this.ownerUUID, true);
            newChange.setCustomData(this.getCustomData());
            data = newChange.toString();
        }

        if (this.hasFile() && !dataclass)
        {
            try (FileWriter objWriter = new FileWriter(this.getCurrentObjectPath().toFile()))
            {
                objWriter.write(data);
            } catch (IOException ioException)
            {
                Constants.getLogger().error("Error on server when trying to process %s from %s ERROR: %s".formatted(getObjectType(), this.ownerUUID, ioException.toString()));
            }
        }
    }

    public boolean create()
    {

        if (!this.hasFile() && !dataclass)
        {
            try {
                Path waypointFilePath = this.getCurrentObjectPath();
                if (waypointFilePath != null)
                {
                    Files.createFile(waypointFilePath);
                    FileWriter waypointFileWriter = new FileWriter(waypointFilePath.toFile());
                    waypointFileWriter.write(this.getRawString());
                    waypointFileWriter.close();

                    return true;
                } else {
                    // PlayerNetworkingHelper.sendUserMessage(this.ownerUUID, "error.jmws.invalid_name", false, JMWSMessageType.FAILURE); TODO: PACKET
                    return false;
                }

            } catch (NoSuchFileException noSuchFileException) {
                CommonClass._createServerResources();
                Constants.getLogger().warn("`jmws` folder was not found so another was made (%s error)".formatted(getObjectType()));
                return create();

            } catch (FileSystemException missingPerms) {
                Constants.getLogger().error("JMWS is missing write permissions to \"jmws\" folder. (%s error)".formatted(getObjectType()));
                return false;

            } catch (IOException genericIOError) {
                Constants.getLogger().error("Got exception trying to make %s -> ".formatted(getObjectType()) + genericIOError);
                return false;
            }
        }
        return false;
    }

    public void share(WSPlayer us, WSPlayer player) {
        new ActionPacket(CommandFactory.makeObjectShareRequestForUser(this.rawPacketData, this.ownerUUID, player.getUUID(), CommandFactory.Direction.FOR_CLIENT, getObjectType()), player).send();
        new ActionPacket(CommandFactory.makeObjectShareRequestForUser(this.rawPacketData, player.getUUID(), this.ownerUUID, CommandFactory.Direction.FOR_HOST, getObjectType()), us).send();
    }

    public void stopSharing(UUID user)
    {
        this.syncing.removeUserFromShare(String.valueOf(user));
        this.accessorSharing.removeFromShared(this.syncing.objectIdentifier, getObjectType());
    }

    public void stopSharing()
    {
        for (String userUUID : this.syncing.sharedTo)
        {
            removeObjectFromUser(UUID.fromString(userUUID), this.syncing.objectIdentifier);
        }
    }

    public UUID getOwnerUUID()
    {
        return ownerUUID;
    }

    @Override
    public String toString()
    {
        return payload.toString();
    }

    public String getObjectNonDuplicateIdentifier()
    {
        return "%s (%s)".formatted(this.getName(), this.getDifferentiator());
    }
}
