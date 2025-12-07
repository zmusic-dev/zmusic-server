package me.zhenxin.zmusic.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

public class EventVelocity {

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Event.onJoin(event.getPlayer());
    }
}
