# IPaint Application

The IPaint Application does the following:

- Draws Rectangle, Ellipse, and Triangle shapes on the canvas
- Draws the shapes with various colors
- Draws the shapes with shading types of either, filled in, filled in and outline, or outline

** Class: SE 450 – Object Oriented Software Development**

# Design Patterns used:
[Command Design Pattern](#cmmand)

[Null Object Design Pattern](#n-obj)

[Static Factory Design Pattern](#s-fact)

[Strategy Design Pattern](#strat)

[Composite Design Pattern](#compos)

[Observer Design Pattern](#obser)

[Singleton Design Pattern](#sngl)


---

---

<a name="cmmand"/> 
# # Command Design Pattern

The command pattern encapsulates in an object all the data required for performing a given action (command), including what method to call, the method’s arguments, and the object to which the method belongs

The command pattern is used in the application to store all the information required for executing the actions on the canvas. Undo and Redo is also implemented as a part of the command pattern

<a name="n-obj"/>
## Null Object Design Pattern

The Null object pattern is a design pattern that simplifies the use of dependencies that can be undefined
A NullShapeStrategy null object was created as one of the strategies belonging to the IDrawable algorithm

<a name="s-fact"/>
## Static Factory Design Pattern

A Static factory a static method that returns an instance of a class.

There are two static factories in this application, DrawableShapeFactory and ShapeTypeFactory

• There is a DrawableShapeFactory which creates the outline, filled-in, outline and filled in shading types for the shapes.

• There is a ShapeTypeFactory which creates the different types of shapes that can be drawn on the canvas.

• These static factories utilize algorithms created with the strategy pattern. DrawableShapeFactory is responsible for creating a new instance of the IDrawable algorithm at runtime.ShapeTypeFactory is responsible for creating a new instance of the IShapeTypeStrategy algorithm at runtime and produce a shape.

Using a factory makes is easy to change the names of the instantiated classes, since the client code does not have to know how to create a concrete class.

<a name="strat"/>
## Strategy Design Pattern

The Strategy Design Pattern defines a family of algorithms, encapsulate each one, and make them interchangeable

This pattern is used in conjunction with the DrawableShapeFactory and ShapeTypeFactory static factories. A new algorithm is selected at runtime.

<a name="compos"/>
## Composite Design Pattern

The composite pattern was used in the application because the commands needed to work the same on groups.

This pattern solved the problem where the difference between individual shapes and shapes that are grouped together needed to be ignored.

##Observer Design Pattern
The Observer defines a one-to-many relationship so that when one object changes state, the others are notified and updated

The observer pattern is used in this project to notify the paintCanvas when changes were made to the shapes. The paintCanvas then redraws the shapes on the canvas

<a name="sngl"/>
## Singleton Design Pattern

The singleton pattern is a software design pattern that restricts the instantiation of a class to one "single" instance. This is useful when exactly one object is needed to coordinate actions across the system.

The singleton design pattern is used to create the object used to store the currently selected shapes on the canvas. I choose to use this pattern instead of a static class so that I could use an interface, as well as have access to methods and properties without having to declare all of them static. The SelectBoundingBox class uses this pattern.
