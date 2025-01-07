# 2D Floor Planner

## Overview
The **2D Floor Planner** is a Java Swing-based desktop application designed for creating and editing 2D floor plans of houses. It provides an intuitive GUI for adding rooms, placing furniture, and designing layouts with various customization options and validations.

## Features

### Room Creation
- Add rooms with customizable dimensions and color-coding.
- Validate room placement to prevent overlaps.

### Relative Positioning
- Place rooms relative to others (e.g., north, south, east, west) with alignment options (left, right, center).
- Check and display errors for invalid placements.

### Drag-and-Drop Editing
- Drag and drop rooms to reposition them on the canvas.
- Validate new positions for overlaps, snapping rooms back if placement is invalid.

### Doors, Windows, and Fixtures
- Add doors and windows with overlap validation:
  - Doors appear as wall openings.
  - Windows appear as dashed lines on walls.
  - Restrictions: No windows between rooms, and certain rooms cannot have external doors.
- Place furniture (e.g., beds, tables, sofas) and fixtures (e.g., sinks, commodes) with rotation in 90-degree steps.

### Plan Management
- Save and reload floor plans using a custom file format.

## Example Screenshot
*(Include an example image or screenshot of the application in action here.)*

## How to Run
1. Ensure you have Java installed on your system.
2. Compile the project using `javac` and run it using `java`.

```bash
cd src
javac -d ../bin *.java
java -cp ../bin DrawingTester
```

