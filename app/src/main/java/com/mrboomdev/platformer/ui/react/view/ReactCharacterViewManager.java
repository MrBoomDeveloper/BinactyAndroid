package com.mrboomdev.platformer.ui.react.view;

import android.os.Bundle;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.mrboomdev.platformer.ui.gameplay.screens.CharacterScreen;
import java.util.Map;

public class ReactCharacterViewManager extends ViewGroupManager<FrameLayout> {
    private ReactApplicationContext context;
    private View view;
	private int width, height;

    public ReactCharacterViewManager(ReactApplicationContext context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return "CharacterView";
    }

    @Override
    public FrameLayout createViewInstance(ThemedReactContext reactContext) {
        return new FrameLayout(reactContext);
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("create", 1);
    }

    @Override
    public void receiveCommand(@NonNull FrameLayout root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        int reactNativeViewId = args.getInt(0);
        int commandIdInt = Integer.parseInt(commandId);

        switch (commandIdInt) {
            case 1:
                createFragment(root, reactNativeViewId);
                break;
        }
    }

    public void createFragment(FrameLayout root, int reactNativeViewId) {
        final Fragment myFragment = new Fragment();
        FragmentActivity activity = (FragmentActivity) context.getCurrentActivity();
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(reactNativeViewId, myFragment, String.valueOf(reactNativeViewId))
            .commit();
    }

    private class Fragment extends AndroidFragmentApplication {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return initializeForView(new CharacterScreen());
        }
    }
}