package com.nashlincoln.blink.model;

import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.util.Log;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.nfc.NfcCommand;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import com.nashlincoln.blink.event.Event;
// KEEP INCLUDES END
/**
 * Entity mapped to table BLINK_GROUP.
 */
public class Group {

    private String name;
    private Integer state;
    private Long id;
    private String attributableType;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient GroupDao myDao;

    private List<GroupDevice> groupDeviceList;
    private List<Attribute> attributes;

    // KEEP FIELDS - put your custom fields here
    private static final String TAG = "Group";
    public static final String KEY = "Group";
    public static final String ATTRIBUTABLE_TYPE = "Group";
    // KEEP FIELDS END

    public Group() {
    }

    public Group(Long id) {
        this.id = id;
    }

    public Group(String name, Integer state, Long id, String attributableType) {
        this.name = name;
        this.state = state;
        this.id = id;
        this.attributableType = attributableType;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGroupDao() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributableType() {
        return attributableType;
    }

    public void setAttributableType(String attributableType) {
        this.attributableType = attributableType;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<GroupDevice> getGroupDeviceList() {
        if (groupDeviceList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupDeviceDao targetDao = daoSession.getGroupDeviceDao();
            List<GroupDevice> groupDeviceListNew = targetDao._queryGroup_GroupDeviceList(id);
            synchronized (this) {
                if(groupDeviceList == null) {
                    groupDeviceList = groupDeviceListNew;
                }
            }
        }
        return groupDeviceList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetGroupDeviceList() {
        groupDeviceList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Attribute> getAttributes() {
        if (attributes == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttributeDao targetDao = daoSession.getAttributeDao();
            List<Attribute> attributesNew = targetDao._queryGroup_Attributes(id, attributableType);
            synchronized (this) {
                if(attributes == null) {
                    attributes = attributesNew;
                }
            }
        }
        return attributes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAttributes() {
        attributes = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    public void flushAttributes() {
        AttributeDao attributeDao = BlinkApp.getDaoSession().getAttributeDao();
        for (Attribute attribute : getAttributes()) {
            attribute.setAttributableId(id);
            attribute.setAttributableType(ATTRIBUTABLE_TYPE);
            attributeDao.insertOrIgnore(attribute);
        }
    }

    public void flushGroupDevices() {
        GroupDeviceDao groupDeviceDao = BlinkApp.getDaoSession().getGroupDeviceDao();
        List<GroupDevice> groupDevices = groupDeviceDao.queryBuilder()
                .where(GroupDeviceDao.Properties.GroupId.eq(getId())).list();

        Set<Long> deviceSet = new HashSet<>();
        for (GroupDevice groupDevice : getGroupDeviceList()) {
            deviceSet.add(groupDevice.getDeviceId());
        }

        Log.d(TAG, "flushGroupDevices: " + groupDevices.size());
        Log.d(TAG, "flushGroupDevices: " + getGroupDeviceList().size());

        for (GroupDevice groupDevice : groupDevices) {
            if (!deviceSet.remove(groupDevice.getDeviceId())) {
                Log.d(TAG, "delete: " + groupDevice.getDeviceId());
                groupDevice.delete();
            }
        }

        for (GroupDevice groupDevice : getGroupDeviceList()) {
            if (deviceSet.contains(groupDevice.getDeviceId())) {
                Log.d(TAG, "adding: " + groupDevice.getDeviceId());
                groupDeviceDao.insertOrReplace(groupDevice);
            }
        }
    }

    public void deleteWithReferences() {
        BlinkApp.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (GroupDevice groupDevice : getGroupDeviceList()) {
                    groupDevice.delete();
                }

                for (Attribute attribute : getAttributes()) {
                    attribute.delete();
                }

                delete();
            }
        });

        Event.broadcast(Group.KEY);
    }

    public static Group newInstance() {
        Group group = new Group();
        group.setAttributableType(ATTRIBUTABLE_TYPE);
        return group;
    }

    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<>();
        for (GroupDevice groupDevice : getGroupDeviceList()) {
            devices.add(groupDevice.getDevice());
        }
        return devices;
    }

    public void setLevel(final int level) {
        BlinkApp.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Attribute attribute = getAttributes().get(1);
                String newValue = String.valueOf(level);
                attribute.setValueLocal(newValue);
                attribute.update();
                state = BlinkApp.STATE_UPDATED;
                update();

                Long attributeTypeId = attribute.getAttributeTypeId();
                for (GroupDevice groupDevice : getGroupDeviceList()) {
                    groupDevice.getDevice().setAttribute(attributeTypeId, newValue);
                }
            }
        });
    }

    public void setOn(final boolean on) {
        BlinkApp.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Attribute attribute = getAttributes().get(0);
                String newValue = on ? Attribute.ON : Attribute.OFF;
                attribute.setValueLocal(newValue);
                attribute.update();
                state = BlinkApp.STATE_UPDATED;
                update();

                Long attributeTypeId = attribute.getAttributeTypeId();
                for (GroupDevice groupDevice : getGroupDeviceList()) {
                    groupDevice.getDevice().setAttribute(attributeTypeId, newValue);
                }
            }
        });
    }

    public boolean isOn() {
        if (getAttributes().size() < 1) {
            return false;
        }
        return getAttributes().get(0).getBool();
    }

    public int getLevel() {
        if (getAttributes().size() < 2) {
            return 0;
        }
        return getAttributes().get(1).getInt();
    }

    public String toNfc() {
        List<NfcCommand> commands = new ArrayList<>();
        List<NfcCommand.Update> updates = new ArrayList<>();
        NfcCommand command = new NfcCommand();
        command.g = getId();
        command.u = updates;
        commands.add(command);

        for (Attribute attribute : getAttributes()) {
            NfcCommand.Update update = new NfcCommand.Update();
            update.i = attribute.getAttributeTypeId();
            update.v = attribute.getValue();
            updates.add(update);
        }
        return BlinkApi.getGson().toJson(commands);
    }

    public void setNominal() {
        switch (state) {
            case BlinkApp.STATE_UPDATED:
                for (Attribute attribute : getAttributes()) {
                    attribute.onSync();
                }
                state = BlinkApp.STATE_NOMINAL;
                update();
                break;

            case BlinkApp.STATE_REMOVED:
                deleteWithReferences();
                break;

            case BlinkApp.STATE_ADDED:
                state = BlinkApp.STATE_NOMINAL;
                update();
                break;

            case BlinkApp.STATE_NAME_SET:
                state = BlinkApp.STATE_NOMINAL;
                update();
                break;
        }
    }

    public static void addNewGroup(String name, final long[] ids) {
        final Group group = Group.newInstance();
        group.setName(name);
        group.setState(BlinkApp.STATE_ADDED);
        BlinkApp.getDaoSession().getGroupDao().insert(group);
        Event.broadcast(Group.KEY);
        Event.observe(Group.KEY, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Event.ignore(Group.KEY, this);

                for (long id : ids) {
                    group.addDevice(id);
                }
            }
        });
        Syncro.getInstance().syncDevices();
    }

    public void onEdit(final String newName, final long[] ids) {
        Log.d(TAG, "onEdit: " + newName + " " + ids.length);
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                if (!getName().equals(newName)) {
                    setName(newName);
                    setState(BlinkApp.STATE_NAME_SET);
                    update();
                }
                setDeviceIds(ids);
                Syncro.getInstance().syncDevices();
            }
        });
    }

    private void setDeviceIds(long[] deviceIds) {
        Set<Long> deviceSet = new HashSet<>();
        for (long deviceId : deviceIds) {
            deviceSet.add(deviceId);
        }

        for (GroupDevice groupDevice : getGroupDeviceList()) {
            if (!deviceSet.remove(groupDevice.getDeviceId())) {
                groupDevice.setState(BlinkApp.STATE_REMOVED);
                groupDevice.update();
            }
        }

        for (Long deviceId : deviceSet) {
            addDevice(deviceId);
        }

        resetGroupDeviceList();
        Event.broadcast(Group.KEY);
    }

    private void addDevice(long deviceId) {
        GroupDevice groupDevice = new GroupDevice();
        groupDevice.setDeviceId(deviceId);
        groupDevice.setGroupId(getId());
        groupDevice.setState(BlinkApp.STATE_ADDED);
        daoSession.getGroupDeviceDao().insertOrReplace(groupDevice);
    }
    // KEEP METHODS END

}