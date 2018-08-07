package eu.quelltext.mundraub.plant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import eu.quelltext.mundraub.api.API;

public class PlantOnlineState {

    public static final String JSON_CLASS = "type";
    public static final String JSON_CLASS_OFFLINE = "offline";
    public static final String JSON_CLASS_ONLINE = "online";
    public static final String JSON_ID = "id";

    public interface OnlineAction {

        boolean mustLogin();
        boolean canCreate();
        boolean canUpdate();
        boolean hasURL();
        boolean canDelete();

        void create(API.Callback cb);
        void update(API.Callback cb);
        URL getURL();
        void delete(API.Callback cb);
        JSONObject toJSON() throws JSONException;
    }

    static private class OfflineState implements OnlineAction {

        private final Plant plant;
        private final API api;

        private OfflineState(Plant plant) {
            this.plant = plant;
            this.api = API.instance();
        }

        @Override
        public boolean mustLogin() {
            return !api.isLoggedIn();
        }

        @Override
        public boolean canCreate() {
            return plant.hasRequiredFieldsFilled() && !mustLogin();
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public boolean hasURL() {
            return false;
        }

        @Override
        public boolean canDelete() {
            return false;
        }

        @Override
        public void create(API.Callback cb) {
            if (!canCreate()) {
                cb.onFailure();
                return;
            }

        }

        @Override
        public void update(API.Callback cb) {
            cb.onFailure();
        }
        @Override
        public URL getURL() {
            return null;
        }
        @Override
        public void delete(API.Callback cb) {
            cb.onFailure();
        }
        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(JSON_CLASS, JSON_CLASS_OFFLINE);
            return json;
        }

        private static OnlineAction fromJSON(Plant plant, JSONObject json) {
            return new OfflineState(plant);
        }
    }

    static private class OnlineState implements OnlineAction {
        private final Plant plant;
        private final API api;
        private final String id;

        private OnlineState(Plant plant, String id) {
            this.plant = plant;
            this.api = API.instance();
            this.id = id;
        }

        @Override
        public boolean mustLogin() {
            return !api.isLoggedIn();
        }

        @Override
        public boolean canCreate() {
            return false;
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public boolean hasURL() {
            return getURL() != null;
        }

        @Override
        public boolean canDelete() {
            return false;
        }

        @Override
        public void create(API.Callback cb) {

        }

        @Override
        public void update(API.Callback cb) {

        }

        @Override
        public URL getURL() {
            try {
                return new URL("https://mundraub.org/map?nid=" + id);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void delete(API.Callback cb) {
        }

        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(JSON_CLASS, JSON_CLASS_ONLINE);
            json.put(JSON_ID, id);
            return json;
        }

        private static OnlineAction fromJSON(Plant plant, JSONObject json) throws JSONException {
            String onlineId = json.getString(JSON_ID);
            return new OnlineState(plant, onlineId);
        }
    }

    public static OfflineState getOfflineState(Plant plant) {
        return new OfflineState(plant);
    }
    public static OnlineAction fromJSON(Plant plant, JSONObject json) throws JSONException {
        String state = json.getString(JSON_CLASS);
        if (state == JSON_CLASS_ONLINE) {
            return OnlineState.fromJSON(plant, json);
        }
        return OfflineState.fromJSON(plant, json);
    }

}
