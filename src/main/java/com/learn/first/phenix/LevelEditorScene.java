package com.learn.first.phenix;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("in Level Editor scene");
    }

    @Override
    public void update(float dt) {
        System.out.println((1.0f / dt) + "FPS");
        if(!changingScene && KeyListener.keyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }

        if(changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.get().r -= dt * 5.0f;
            Window.get().g -= dt * 5.0f;
            Window.get().b -= dt * 5.0f;
        } else if(changingScene) {
            Window.changeScene(1);
        }
    }
}
