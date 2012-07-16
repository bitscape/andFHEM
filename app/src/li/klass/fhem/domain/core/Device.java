/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.domain.core;

import android.util.Log;
import li.klass.fhem.AndFHEMApplication;
import li.klass.fhem.R;
import li.klass.fhem.appwidget.view.widget.AppWidgetView;
import li.klass.fhem.domain.FileLogDevice;
import li.klass.fhem.domain.floorplan.Coordinate;
import li.klass.fhem.domain.floorplan.FloorplanPosition;
import li.klass.fhem.domain.genericview.ShowField;
import li.klass.fhem.util.StringEscapeUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Device<T extends Device> implements Serializable, Comparable<T> {

    protected String room;

    protected String name;

    private String state;

    protected String alias;

    @ShowField(description = R.string.measured)
    protected String measured;

    @ShowField(description = R.string.definition)
    protected String definition;
    protected Map<String, String> eventMapReverse = new HashMap<String, String>();
    protected Map<String, String> eventMap = new HashMap<String, String>();
    private String[] availableTargetStates;

    private Map<String, FloorplanPosition> floorPlanPositionMap = new HashMap<String, FloorplanPosition>();

    protected volatile FileLogDevice fileLog;
    private List<DeviceChart> deviceCharts = new ArrayList<DeviceChart>();

    public void loadXML(Node xml) {

        Node setsAttribute = xml.getAttributes().getNamedItem("sets");
        if (setsAttribute != null) {
            String setsText = setsAttribute.getNodeValue().replaceAll("\\*", "");
            parseSetsAttribute(setsText);
        }

        NodeList childNodes = xml.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item == null || item.getAttributes() == null) continue;

            Node keyAttribute = item.getAttributes().getNamedItem("key");
            if (keyAttribute == null) continue;

            String keyValue = keyAttribute.getNodeValue().toUpperCase().trim();
            String nodeContent = item.getAttributes().getNamedItem("value").getNodeValue().trim();
            nodeContent = StringEscapeUtils.unescape(nodeContent);

            if (nodeContent == null || nodeContent.length() == 0) {
                continue;
            }

            try {
                parseNodeContent(item, keyValue, nodeContent);
            } catch (Exception e) {
                Log.e(Device.class.getName(), "an error occurred while reading attribute " + keyValue + " with value" +
                        nodeContent, e);
            }
        }

        NamedNodeMap attributes = xml.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attributeNode = attributes.item(i);
            onAttributeRead(attributeNode.getNodeName().toUpperCase(), attributeNode.getNodeValue());
        }

        fillDeviceCharts(deviceCharts);

        afterXMLRead();

    }

    private void parseSetsAttribute(String setsText) {
        setsText = setsText.trim();
        String lowercase = setsText.toLowerCase();
        if (lowercase.equals("") || lowercase.equals("*") || lowercase.contains("not set function")
                || lowercase.contains("needs one parameter")) {
            return;
        }

        this.availableTargetStates = setsText.split(" ");
    }

    private void parseNodeContent(Node item, String keyValue, String nodeContent) {
        if (keyValue.equals("ROOM")) {
            room = nodeContent;
        } else if (keyValue.equals("NAME")) {
            name = nodeContent;
        } else if (state == null && keyValue.equals("STATE")) {
            state = nodeContent;
        } else if (keyValue.equals("ALIAS")) {
            alias = nodeContent;
        } else if (keyValue.equals("CUL_TIME")) {
            measured = nodeContent;
        } else if (keyValue.equals("DEF")) {
            definition = nodeContent;
        } else if (keyValue.equals("EVENTMAP")) {
            parseEventMap(nodeContent);
        } else if (keyValue.startsWith("FP_")) {
            String[] commaParts = nodeContent.split(",");
            if (commaParts.length > 2) {
                int y = Integer.valueOf(commaParts[0]);
                int x = Integer.valueOf(commaParts[1]);
                int viewType = Integer.valueOf(commaParts[2]);

                floorPlanPositionMap.put(keyValue.substring(3), new FloorplanPosition(x, y, viewType));
            }
        }

        String tagName = item.getNodeName().toUpperCase();
        onChildItemRead(tagName, keyValue, nodeContent, item.getAttributes());
    }

    protected void afterXMLRead() {}

    private void parseEventMap(String content) {
        eventMap = new HashMap<String, String>();
        eventMapReverse = new HashMap<String, String>();

        if (content.startsWith("/")) {
            parseSlashesEventMap(content);
        } else {
            parseSpacesEventMap(content);
        }
    }

    private void parseSpacesEventMap(String content) {
        String[] events = content.split(" ");
        for (String event : events) {
            String[] eventParts = event.split(":");
            if (eventParts.length < 2) continue;
            putEventToEventMap(eventParts[0], eventParts[1]);
        }
    }

    private void parseSlashesEventMap(String content) {
        String[] events = content.split("/");
        for (int i = 1; i < events.length; i++) {
            String event = events[i];
            String[] eventParts = event.split(":");
            putEventToEventMap(eventParts[0], eventParts[1]);
        }
    }

    private void putEventToEventMap(String key, String value) {
        eventMap.put(key, value);
        eventMapReverse.put(value, key);
    }

    public String getAliasOrName() {
        if (alias != null && alias.length() != 0) {
            return alias;
        }
        return getName();
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        if (room == null) {
            return AndFHEMApplication.getContext().getResources().getString(R.string.unsortedRoomName);
        }
        return room;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasured() {
        return measured;
    }

    /**
     * Called for each device node in the <i>xmllist</i>.
     * @param tagName contains the current tag name (i.e. STATE, ATTR or INT)
     * @param keyValue name of the key (i.e. ROOM)
     * @param nodeContent value of the tag
     * @param attributes additional tag attributes
     */
    protected abstract void onChildItemRead(String tagName, String keyValue, String nodeContent, NamedNodeMap attributes);

    protected void onAttributeRead(String attributeKey, String attributeValue) {
    }

    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", room_list_name='" + room + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return !(name != null ? !name.equals(device.name) : device.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public int compareTo(T t) {
        return getName().compareTo(t.getName());
    }

    public FileLogDevice getFileLog() {
        return fileLog;
    }

    public void setFileLog(FileLogDevice fileLog) {
        this.fileLog = fileLog;
    }

    public List<DeviceChart> getDeviceCharts() {
        return deviceCharts;
    }

    /**
     * Override me if you want to provide charts for a device
     * @param chartSeries fill me with chart descriptions
     */
    protected void fillDeviceCharts(List<DeviceChart> chartSeries) {
    }

    protected void addDeviceChartIfNotNull(Object notNull, DeviceChart holder) {
        if (notNull != null) {
            deviceCharts.add(holder);
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInternalState() {
        String state = getState();
        if (eventMapReverse == null || ! eventMapReverse.containsKey(state)) return state;
        return eventMapReverse.get(state);
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isSupported() {
        return true;
    }

    public Map<String, String> getEventMap() {
        return eventMap;
    }

    public boolean isOnFloorplan(String floorplan) {
        return floorPlanPositionMap.containsKey(floorplan.toUpperCase());
    }

    public FloorplanPosition getFloorplanPositionFor(String floorplan) {
        return floorPlanPositionMap.get(floorplan.toUpperCase());
    }

    public void setCoordinateFor(String floorplan, Coordinate coordinate) {
        String key = floorplan.toUpperCase();
        if (! floorPlanPositionMap.containsKey(key)) return;

        FloorplanPosition floorplanPosition = floorPlanPositionMap.get(key);
        FloorplanPosition newPosition = new FloorplanPosition(coordinate.x, coordinate.y, floorplanPosition.viewType);

        floorPlanPositionMap.put(key, newPosition);
    }

    public String[] getAvailableTargetStates() {
        return availableTargetStates;
    }

    public boolean supportsWidget(Class<? extends AppWidgetView> appWidgetClass) {
        return true;
    }
}