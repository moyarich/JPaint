package model.util;

import java.awt.*;

public class ShapeCollision {

    /**
     * https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection
     * https://www.youtube.com/watch?v=l0KpQY9-YdM
     *
     * Determines whether 2 shapes have collided with each other.
     *
     * @param otherShape The shape use to detecting if there is an intersection between it this shape
     * @return true if  a collision was detected, false if no collision was detected
     *
     */
    public static boolean detect(Shape shape, Shape otherShape) {
        Rectangle rect1 = shape.getBounds();
        Rectangle rect2 = otherShape.getBounds();

        boolean collisionDetected = false;

        if (rect1.x < rect2.x + rect2.width &&
                rect1.x + rect1.width > rect2.x &&
                rect1.y < rect2.y + rect2.height &&
                rect1.y + rect1.height > rect2.y) {

            collisionDetected = true;
        }

        return collisionDetected;
    }
}
