import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class DrawingPanel extends JPanel {
    public java.util.List<Shape> shapes = new java.util.ArrayList<>();
    public java.util.List<ImageShape> fixtures = new java.util.ArrayList<>();
    private Shape draggedShape = null;
    private Point previousPoint;
    public Shape selectedShape = null;
    public int width, height;
    public DrawingPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
            setLayout(null);

        JPopupMenu popupMenu = new JPopupMenu();
        JPopupMenu mainPopup = new JPopupMenu();
        
        //Main Menu
        JMenuItem saveFile = new JMenuItem("Save File");
        JMenuItem loadFile  = new JMenuItem("Load File");
        JMenuItem clearAllShapes = new JMenuItem("Clear All Shapes");
        saveFile.addActionListener(new SaveActionListner(shapes,this));
        loadFile.addActionListener(new LoadActionListener(this));
        // clearAllShapes.addActionListener(new clearallListener(shapes,this));
        clearAllShapes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("OptionPane.messageForeground", Color.WHITE);
                UIManager.put("Button.background", new Color(102,102,102));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
                UIManager.put("OptionPane.background", new Color(50,50,50));
                UIManager.put("Panel.background", new Color(50,50,50));
                UIManager.put("InternalFrame.background", new Color(50,50,50));
                int option = JOptionPane.showConfirmDialog(DrawingPanel.this, "Are you sure you want to clear all shapes?", "Clear All Shapes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    shapes.clear();
                    for(ImageShape s:fixtures){
                        s.DeleteListener();
                    }
                    DrawingTester.setzeroArea();
                    repaint();
                }
            }
        });
        mainPopup.add(saveFile);
        mainPopup.add(loadFile);
        mainPopup.add(clearAllShapes);

        // Shape Menu
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem cloneItem = new JMenuItem("Clone");
        JMenuItem resizeItem = new JMenuItem("Resize");
        JMenuItem editLabel = new JMenuItem("Edit Label");
        JMenuItem editColor = new JMenuItem("Edit Color");
        
        JMenu addFixture = new JMenu("Add fixture");
        JMenuItem bed = new JMenuItem("Bed");
        JMenuItem table = new JMenuItem("Table");
        JMenuItem sofa = new JMenuItem("Sofa");
        JMenuItem dining = new JMenuItem("Dining Set");
        JMenuItem commode = new JMenuItem("Commode");
        JMenuItem basin = new JMenuItem("Basin");
        JMenuItem shower = new JMenuItem("Bathtub");
        JMenuItem ksink = new JMenuItem("Kitchen Sink");
        JMenuItem stove = new JMenuItem("Stove");
        bed.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/bed.png",50,50));
        table.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/table.png",70,70));
        sofa.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/sofa.png",60,60));
        dining.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/dining.png",70,70));
        commode.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/commode.png",40,40));
        basin.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/basin.png",35,35));
        shower.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/bathtub.png",40,40));
        ksink.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/kink.png",60,60));
        stove.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/stove.png",60,60));
        addFixture.add(bed);
        addFixture.add(table);
        addFixture.add(sofa);
        addFixture.add(dining);
        addFixture.add(commode);
        addFixture.add(basin);
        addFixture.add(shower);
        addFixture.add(ksink);
        addFixture.add(stove);


        JMenu addRoomMenu = new JMenu("Add Room");
        JMenuItem addRoomNorth = new JMenuItem("North");
        JMenuItem addRoomSouth = new JMenuItem("South");
        JMenuItem addRoomEast = new JMenuItem("East");
        JMenuItem addRoomWest = new JMenuItem("West");

        deleteItem.addActionListener(new DeleteActionListener(shapes, this));
        cloneItem.addActionListener(new CloneActionListener(shapes, this));
        resizeItem.addActionListener(new ResizeActionListener(this));
        editLabel.addActionListener(new EditLabelActionListener(this));
        editColor.addActionListener(new EditColorActionListener(this));
        
        addRoomNorth.addActionListener(e -> addRoomRelativeToSelected("North"));
        addRoomSouth.addActionListener(e -> addRoomRelativeToSelected("South"));
        addRoomEast.addActionListener(e -> addRoomRelativeToSelected("East"));
        addRoomWest.addActionListener(e -> addRoomRelativeToSelected("West"));

        addRoomMenu.add(addRoomNorth);
        addRoomMenu.add(addRoomSouth);
        addRoomMenu.add(addRoomEast);
        addRoomMenu.add(addRoomWest);
        
        
        popupMenu.add(addFixture);
        popupMenu.add(cloneItem);
        popupMenu.add(deleteItem);
        popupMenu.add(resizeItem);
        popupMenu.add(editLabel);
        popupMenu.add(editColor);
        popupMenu.add(addRoomMenu);
        // Add border modification menu
        JMenu borderMenu = new JMenu("Modify Border");
        String[] sides = {"Top", "Bottom", "Left", "Right"};

        for (String side : sides) {
            JMenu sideMenu = new JMenu(side);
            
            JMenuItem addDoor = new JMenuItem("Add Door");
            addDoor.addActionListener(e -> {
                if (selectedShape != null) {
                    if (selectedShape.canAddDoor(side)) {
                        selectedShape.addDoor(side);
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(
                            null,
                            "Doors in bedrooms/bathrooms can only be added where there is an adjacent room.",
                            "Invalid Door Placement",
                            JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            });
            
            JMenuItem addWindow = new JMenuItem("Add Window");
            addWindow.addActionListener(e -> {
                if (selectedShape != null) {
                    selectedShape.addWindow(side);
                    repaint();
                }
            });
            
            JMenuItem clearBorder = new JMenuItem("Clear");
            clearBorder.addActionListener(e -> {
                if (selectedShape != null) {
                    selectedShape.clearModification(side);
                    repaint();
                }
            });
            
            sideMenu.add(addDoor);
            sideMenu.add(addWindow);
            sideMenu.add(clearBorder);
            borderMenu.add(sideMenu);
        }

        popupMenu.add(borderMenu);

        popupMenu.add(borderMenu);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean shapeSelected = false;
        
                // Handle right-click for showing context menus
                if (SwingUtilities.isRightMouseButton(e)) {
                    for (Shape s : shapes) {
                        if (s.contains(e.getPoint())) {
                            selectedShape = s;
                            popupMenu.show(DrawingPanel.this, e.getX(), e.getY());
                            shapeSelected = true;
                            break;
                        }
                    }
                    if (!shapeSelected) {
                        mainPopup.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // Handle left-click for selecting and dragging shapes
                    for (Shape s : shapes) {
                        if (s.contains(e.getPoint())) {
                            draggedShape = s;
                            previousPoint = e.getPoint();
                            shapeSelected = true;
                            break;
                        }
                    }
                    // If no shape is selected, clear the selectedShape to avoid unwanted movement
                    if (!shapeSelected) {
                        selectedShape = null;
                        draggedShape = null;
                    }
                }
            }
        
            @Override
            public void mouseReleased(MouseEvent e) {
                draggedShape = null;
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedShape != null && SwingUtilities.isLeftMouseButton(e)) {
                    Point currentPoint = e.getPoint();
                    int dx = (int) (currentPoint.getX() - previousPoint.getX());
                    int dy = (int) (currentPoint.getY() - previousPoint.getY());
        
                    if (canMove(draggedShape, dx, dy)) {
                        draggedShape.translate(dx, dy);
                        previousPoint = currentPoint;
                        repaint();
                    }
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }
    private void addRoomRelativeToSelected(String direction) {
        if (selectedShape == null) {
            JOptionPane.showMessageDialog(null, "No room selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        int padding = 5;
        int newX = selectedShape.x;
        int newY = selectedShape.y;

        switch (direction) {
            case "North":
                newY = selectedShape.y - selectedShape.height - padding;
                break;
            case "South":
                newY = selectedShape.y + selectedShape.height + padding;
                break;
            case "East":
                newX = selectedShape.x + selectedShape.width + padding;
                break;
            case "West":
                newX = selectedShape.x - selectedShape.width - padding;
                break;
        }
        Shape newRoom = new Shape(selectedShape.width, selectedShape.height, newX, newY, selectedShape.color, "New Room");
        String lengthString = JOptionPane.showInputDialog(this,"Enter the length of box:","LENGTH",JOptionPane.QUESTION_MESSAGE);
        String breadthString = JOptionPane.showInputDialog(this,"Enter the width of box","WIDTH",JOptionPane.QUESTION_MESSAGE);
        String newLabel = JOptionPane.showInputDialog(this,"Enter the new label:");
        if(lengthString != null && breadthString != null && newLabel != null){
            Color color = JColorChooser.showDialog(this, "Choose a color for the shape", Color.BLACK);
            int length = Integer.parseInt(lengthString)*10;
            int breadth = Integer.parseInt(breadthString)*10;

            newRoom = new Shape(breadth, length, newX, newY, color, newLabel);
        }
        if (!isOverlapping(newRoom)) {
            if (newX > width || newX<0) {
                System.out.println(width);
                System.out.println(newX);
                JOptionPane.showMessageDialog(null, "The new room is going out of bounds", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                shapes.add(newRoom);
                repaint();
            }
            
        } else {
            
            JOptionPane.showMessageDialog(null, "The new room overlaps with an existing room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean isOverlapping(Shape newShape) {
        Rectangle newBounds = new Rectangle(newShape.x, newShape.y, newShape.width, newShape.height);
        for (Shape existingShape : shapes) {
            Rectangle existingBounds = new Rectangle(existingShape.x, existingShape.y, existingShape.width, existingShape.height);
            if (newBounds.intersects(existingBounds)) {
                return true;
            }
        }
        return false;
    }

    
    public void addedShape(){
        repaint();
    }
    public void addShape(Shape shape) {
        // Determine placement for row-major order
        int padding = 10; // Padding between shapes
        int currentX = padding;
        int currentY = padding;
        int maxHeightInRow = 0;
        
        for (Shape s : shapes) {
            // Update currentX to the next position in the row
            if (currentX + s.width + padding > width) {
                currentX = padding;
                currentY += maxHeightInRow + padding;
                maxHeightInRow = 0;
            }
            currentX = s.x + s.width + padding;
            maxHeightInRow = Math.max(maxHeightInRow, s.height);
        }
    
        // If the new shape exceeds the panel width, move to the next row
        if (currentX + shape.width > width) {
            currentX = padding;
            currentY += maxHeightInRow + padding;
        }
    
        // Set the new shape's position
        shape.x = currentX;
        shape.y = currentY;
        shapes.add(shape);
        repaint();
    }

    private boolean canMove(Shape movingShape, int dx, int dy) {
    Rectangle newBounds = new Rectangle(movingShape.x + dx, movingShape.y + dy, movingShape.width, movingShape.height);

    for (Shape otherShape : shapes) {
        if (otherShape != movingShape) {
            Rectangle otherBounds = new Rectangle(otherShape.x, otherShape.y, otherShape.width, otherShape.height);
            if (newBounds.intersects(otherBounds)) {
                return false;
            }
        }
    }
    return true;
    }
    // return true; 
    /*private boolean canMove(Shape movingShape, int dx, int dy) {
        Rectangle newBounds = new Rectangle(
            movingShape.x + dx,
            movingShape.y + dy,
            movingShape.width,
            movingShape.height
        );
        
        for (Shape otherShape : shapes) {
            if (otherShape != movingShape) {
                Rectangle otherBounds = new Rectangle(
                    otherShape.x,
                    otherShape.y,
                    otherShape.width,
                    otherShape.height
                );
                if (newBounds.intersects(otherBounds)) {
                    return false;
                }
            }
        }
        return true;
    }*/
// }

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    // Draw all shapes
    for (Shape s : shapes) {
        s.draw(g2d);
    }

    // Fixtures are managed by Swing components (JLabel) and do not need to be redrawn here
}

    public void addImageShape(ImageShape imageShape) {
        if (!fixtures.contains(imageShape)) {
            fixtures.add(imageShape);
            this.add(imageShape.getImageLabel());
        }
        repaint();
    }
}

class SaveActionListner implements ActionListener{
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public SaveActionListner(java.util.List<Shape> shapes, DrawingPanel drawingPanel){
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        System.out.println(DrawingTester.floors.size());
        List<List<Map<String, Object>>> savefile = new ArrayList<>();
        for (DrawingPanel p : DrawingTester.floors){    
        // System.out.println(p.shapes);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Shape s : p.shapes){
            Map<String, Object> obj1 = new HashMap<>();
            
            obj1.put("type", "shape");
            obj1.put("x",String.valueOf(s.x));
            obj1.put("y",String.valueOf(s.y));
            obj1.put("width",String.valueOf(s.width));
            obj1.put("height",String.valueOf(s.height));
            // Save color as RGB values
            obj1.put("color", s.color.getRGB());
            obj1.put("room_label",String.valueOf(s.room_label));
            obj1.put("side_modi",s.sideModification);
            data.add(obj1);
            
        }
        for (ImageShape is: p.fixtures){
            Map<String, Object> obj1 = new HashMap<>();
            obj1.put("type", "fixture");
            obj1.put("x", is.x);
            obj1.put("y", is.y);
            obj1.put("width", is.width);
            obj1.put("height", is.height);
            obj1.put("path", is.getPath());
            data.add(obj1);

        }
            savefile.add(data);
        }
        try {
            String filename = JOptionPane.showInputDialog(drawingPanel, "Enter the filename to save the file as:");
            boolean extension = filename.endsWith(".rmap");
            if (extension){
                RMapFile.writeRMap(filename, savefile);
                JOptionPane.showMessageDialog(null, "File saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);  
            }
            else{
                JOptionPane.showMessageDialog(null, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Or handle the exception as appropriate for your application
        }
    }
}

// class clearallListener implements ActionListener{
//     private java.util.List<Shape> shapes;
//     private DrawingPanel drawingPanel;

//     public clearallListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel){
//         this.shapes = shapes;
//         this.drawingPanel = drawingPanel;
//     }
//     @Override
//     public void actionPerformed(ActionEvent e)
// }
class LoadActionListener implements  ActionListener{
    // private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;
    // private DrawingTester drawingTester;


    public LoadActionListener(DrawingPanel drawingPanel) {
        // this.shapes = shapes;
        this.drawingPanel = drawingPanel;
        // this.drawingTester = drawingTester;

    }

    @Override
    public  void actionPerformed(ActionEvent e){
        try{
            UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Button.background", new Color(102,102,102));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
            UIManager.put("OptionPane.background", new Color(50,50,50));
            UIManager.put("Panel.background", new Color(50,50,50));
            UIManager.put("InternalFrame.background", new Color(50,50,50));
        String filename = JOptionPane.showInputDialog(drawingPanel, "Enter the filename to load the file from:");
        boolean extension = filename.endsWith(".rmap");
        if (extension){
            List<List<Map<String, Object>>> readData = RMapFile.readRMap(filename);
            DrawingTester.floors.clear();
            // System.out.println(readData);
            for (ImageShape i: drawingPanel.fixtures){
                i.DeleteListener();
            }
            for(List<Map<String,Object>> inner : readData){
                DrawingPanel drawp = new DrawingPanel(2000, 2000);
                drawp.setBackground(new Color(75, 75, 75));
                drawp.shapes = new ArrayList<>();
                drawp.fixtures = new ArrayList<>();
                            
                        for (Map<String, Object> obj : inner){
                            String type = (String) obj.get("type");
                            if ("shape".equals(type)){
                            int x = Integer.parseInt((String) obj.get("x"));
                            int y = Integer.parseInt((String) obj.get("y"));
                            int width = Integer.parseInt((String) obj.get("width"));
                            int height = Integer.parseInt((String) obj.get("height"));
                            Map<String, String> hashMap = (Map<String, String>) obj.get("side_modi");
                            // Load color from RGB value
                            Color color = new Color((int) obj.get("color"));
                            String room_label = (String) obj.get("room_label");
                            Shape k =new Shape(width, height, x, y, color, room_label);
                            k.sideModification = hashMap;
                            drawp.shapes.add(k);
                            }
                            else{
                                
                            }
                        }for (Map<String, Object> obj : inner){
                            String type = (String) obj.get("type");
                            if ("fixture".equals(type)){
                                int x = (int) obj.get("x");
                                int y = (int) obj.get("y");
                                int width = (int) obj.get("width");
                                int height = (int) obj.get("height");
                                String path = (String) obj.get("path");
                                ImageShape imageShape = new ImageShape(x, y, width, height, path);
                                // drawp.fixtures.add(imageShape);
                                drawp.add(imageShape.getImageLabel());
                                for (Shape shape : drawp.shapes) {
                                    if (shape.contains(new Point(x, y))) {
                                        shape.addLinkedImage(imageShape);
                                        break; // Assuming each fixture only belongs to one shape
                                    }
                                }
                            }
                        }
                DrawingTester.floors.add(drawp);
                // drawp.repaint();
            }
            // drawingPanel.repaint();
            DrawingTester.refreshUI();
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
        }
        drawingPanel.repaint();
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);  
            ex.printStackTrace();
        }

    }
}
class DeleteActionListener implements ActionListener {
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public DeleteActionListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel) {
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this shape?", "Delete Shape", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            shapes.remove(drawingPanel.selectedShape);
            DrawingTester.updateTotalAreaLabel(drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
            drawingPanel.selectedShape = null;
            drawingPanel.repaint();
        }
    }
}

class CloneActionListener implements ActionListener {
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public CloneActionListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel) {
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
public void actionPerformed(ActionEvent e) {
    // Modify CloneActionListener to add cloned shape in row-major order
    Shape originalShape = drawingPanel.selectedShape;
    if (originalShape != null) {
        Shape clonedShape = new Shape(
            originalShape.width,
            originalShape.height,
            0, // Temporary x, will be set by addShape
            0, // Temporary y, will be set by addShape
            originalShape.color,
            originalShape.room_label
        );

        // Check for overlap before adding the cloned shape
        boolean overlap = false;
        for (Shape s : drawingPanel.shapes) {
            if (s != originalShape && s.contains(new Point(clonedShape.x, clonedShape.y))) {
                overlap = true;
                break;
            }
        }

        if (!overlap) {
            drawingPanel.addShape(clonedShape);
            DrawingTester.updateTotalAreaLabel(-originalShape.width * originalShape.height / 100);
            drawingPanel.repaint();
        }
    }
    
}
    
}


class FixtureAddActionListner implements ActionListener {
    private java.util.List<ImageShape> fixtures;
    private DrawingPanel drawingPanel;
    private String path;
    int w;
    int h;
    public FixtureAddActionListner(java.util.List<ImageShape> fixtures, DrawingPanel drawingPanel, String path,int w,int h) {
        this.fixtures = fixtures;
        this.drawingPanel = drawingPanel;
        this.path = path;
        this.w =w;
        this.h =h;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Shape originalShape = drawingPanel.selectedShape;
        if (originalShape != null) {
            ImageShape imageShape = new ImageShape(originalShape.x, originalShape.y, w, h, path);
            if (imageShape.getImage() == null) {
                System.out.println("Image not found: " + path);
            } else {
                System.out.println("Image loaded successfully: " + path);
                drawingPanel.addImageShape(imageShape);
                originalShape.addLinkedImage(imageShape); // Link the image to the shape
            }
        } else {
            JOptionPane.showMessageDialog(drawingPanel, "Please select a shape to add a fixture to.", "No Shape Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}

class ResizeActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public ResizeActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        
        
        String lengthString = JOptionPane.showInputDialog(drawingPanel,"Enter the length of box:","LENGTH",JOptionPane.QUESTION_MESSAGE);
        String breadthString = JOptionPane.showInputDialog(drawingPanel,"Enter the width of box","WIDTH",JOptionPane.QUESTION_MESSAGE);

        
        
        if (lengthString != null && breadthString != null){
        int length = Integer.parseInt(lengthString)*10;
        int breadth = Integer.parseInt(breadthString)*10;
        DrawingTester.updateTotalAreaLabel(drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.selectedShape.width = length;
        drawingPanel.selectedShape.height = breadth;
        DrawingTester.updateTotalAreaLabel(-drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.repaint();
        }
        else{
            JOptionPane.showMessageDialog(null, "Enter valid values for length and breadth.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class EditLabelActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public EditLabelActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        
        String newLabel = JOptionPane.showInputDialog(drawingPanel,"Enter the new label:");
        if (newLabel != null){
            drawingPanel.selectedShape.room_label = newLabel;
            drawingPanel.repaint();
        }
        else{
            JOptionPane.showMessageDialog(null, "No label entered.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class EditColorActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public EditColorActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){        
        Color color = JColorChooser.showDialog(drawingPanel, "Choose a color for the shape", Color.BLACK);
        drawingPanel.selectedShape.color = color;
        drawingPanel.repaint();
    }
}

class Shape {
    int width, height;
    int x, y;
    int x_label, y_label;
    public Color color;
    public String room_label;
    private java.util.List<ImageShape> linkedImages = new java.util.ArrayList<>();
    Map<String, String> sideModification = new HashMap<>();
    public Shape(int w, int h, int x, int y, Color color, String room_label) {
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
        this.color =  color;
        this.room_label = room_label;

        
    }

    public void draw(Graphics2D g2d) {
        // Fill the rectangle
        g2d.setColor(color);
        g2d.fill(new Rectangle2D.Double(x, y, width, height));
        
        // Draw the black border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Draw each border separately
        // Top border
        if (sideModification.getOrDefault("Top", "").equals("Gap")) {
            // Draw solid black lines on the sides
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x + width/4, y);
            g2d.drawLine(x + width*3/4, y, x + width, y);
            // Draw white gap in the middle
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(x + width/4+2, y+2, x + width*3/4-2, y+2);
        } else if (sideModification.getOrDefault("Top", "").equals("Window")) {
            // Draw solid black lines on the sides
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x + width/4, y);
            g2d.drawLine(x + width*3/4, y, x + width, y);
            // Draw dashed line in the middle
            float[] dashPattern = {10, 5};
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(x + width/4, y, x + width*3/4, y);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x + width, y);
        }
        
        // Bottom border
        if (sideModification.getOrDefault("Bottom", "").equals("Gap")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + height, x + width/4, y + height);
            g2d.drawLine(x + width*3/4, y + height, x + width, y + height);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(x + width/4+2, y + height-2, x + width*3/4-2, y + height-2);
        } else if (sideModification.getOrDefault("Bottom", "").equals("Window")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + height, x + width/4, y + height);
            g2d.drawLine(x + width*3/4, y + height, x + width, y + height);
            float[] dashPattern = {10, 5};
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(x + width/4, y + height, x + width*3/4, y + height);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + height, x + width, y + height);
        }
        
        // Left border
        if (sideModification.getOrDefault("Left", "").equals("Gap")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x, y + height/4);
            g2d.drawLine(x, y + height*3/4, x, y + height);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(x+2, y + height/4+2, x+2, y + height*3/4-2);
        } else if (sideModification.getOrDefault("Left", "").equals("Window")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x, y + height/4);
            g2d.drawLine(x, y + height*3/4, x, y + height);
            float[] dashPattern = {10, 5};
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(x, y + height/4, x, y + height*3/4);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y, x, y + height);
        }
        
        // Right border
        if (sideModification.getOrDefault("Right", "").equals("Gap")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x + width, y, x + width, y + height/4);
            g2d.drawLine(x + width, y + height*3/4, x + width, y + height);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(x + width-2, y + height/4+2, x + width-2, y + height*3/4-2);
        } else if (sideModification.getOrDefault("Right", "").equals("Window")) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x + width, y, x + width, y + height/4);
            g2d.drawLine(x + width, y + height*3/4, x + width, y + height);
            float[] dashPattern = {10, 5};
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(x + width, y + height/4, x + width, y + height*3/4);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x + width, y, x + width, y + height);
        }
        
        // Draw the room label
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        int x_label = x + (width - g2d.getFontMetrics().stringWidth(room_label)) / 2;
        int y_label = y + ((height - g2d.getFontMetrics().getHeight()) / 2) + g2d.getFontMetrics().getAscent();
        g2d.drawString(room_label, x_label, y_label);
    }
    
    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
        for (ImageShape linkedImage : linkedImages) {
            linkedImage.translate(dx, dy);
        }
    }public boolean canAddDoor(String side) {
        // Check if the room is a bedroom or bathroom
        if (room_label.toLowerCase().contains("bedroom") || 
            room_label.toLowerCase().contains("bathroom")) {
            
            // Get coordinates of the side where we want to add the door
            Rectangle sideBounds = getSideBounds(side);
            
            // Check if there's an adjacent room on this side
            boolean hasAdjacentRoom = false;
            for (Shape other : DrawingTester.floors.get(DrawingTester.getCurrentFloor()).shapes) {
                if (other != this) {
                    Rectangle otherBounds = new Rectangle(other.x, other.y, other.width, other.height);
                    if (sideBounds.intersects(otherBounds)) {
                        hasAdjacentRoom = true;
                        break;
                    }
                }
            }
            
            return hasAdjacentRoom;
        }
        
        // For non-bedroom/bathroom rooms, always allow doors
        return true;
    }

    private Rectangle getSideBounds(String side) {
        // Create a thin rectangle along the specified side
        int tolerance = 2; // Tolerance for intersection detection
        switch (side) {
            case "Top":
                return new Rectangle(x, y - tolerance, width, tolerance * 2);
            case "Bottom":
                return new Rectangle(x, y + height - tolerance, width, tolerance * 2);
            case "Left":
                return new Rectangle(x - tolerance, y, tolerance * 2, height);
            case "Right":
                return new Rectangle(x + width - tolerance, y, tolerance * 2, height);
            default:
                return new Rectangle();
        }
    }

    public void addDoor(String side) {
        if (canAddDoor(side)) {
            sideModification.put(side, "Gap");
        }
    }

    public void addWindow(String side) {
        sideModification.put(side, "Window");
    }

    public void clearModification(String side) {
        sideModification.remove(side);
    }
    public void addLinkedImage(ImageShape imageShape) {
        this.linkedImages.add(imageShape);
    }
}
class ImageShape {
    int x, y;
    int width, height;
    JLabel imageLabel;
    Point initialClick;
    Image originalImage;
    double rot = 0.0;
    String path;
    public ImageShape(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.path = imagePath;

        // Load and resize the image to match the specified width and height
        ImageIcon originalIcon = new ImageIcon(imagePath);
        originalImage = originalIcon.getImage(); // Set the original image
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        // Create a JLabel with the resized image
        imageLabel = new JLabel(scaledIcon){
            @Override
            protected void paintComponent(Graphics g) {
                // super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.rotate(rot, scaledIcon.getIconWidth() / 2, scaledIcon.getIconHeight() / 2);
                g2.drawImage(scaledIcon.getImage(), 0, 0, null);
            }
        };
        imageLabel.setBounds(x, y, width, height);

        // Add mouse listeners for dragging and rotating the image
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    initialClick = e.getPoint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Show a context menu instead of changing position
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    JMenuItem rotateLeftItem = new JMenuItem("Rotate Left");
                    JMenuItem rotateRightItem = new JMenuItem("Rotate Right");

                    deleteItem.addActionListener(event -> DeleteListener());

                    rotateLeftItem.addActionListener(event -> rotateImage(-90));
                    rotateRightItem.addActionListener(event -> rotateImage(90));

                    popupMenu.add(deleteItem);
                    popupMenu.add(rotateLeftItem);
                    popupMenu.add(rotateRightItem);
                    popupMenu.show(imageLabel, e.getX(), e.getY());
                    e.consume(); // Prevent unintended side effects
                }
            }
        });

        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Get the current location of the label
                    int thisX = imageLabel.getX();
                    int thisY = imageLabel.getY();

                    // Determine how much the mouse moved since the initial click
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    // Move the label to the new location
                    int nextX = thisX + xMoved;
                    int nextY = thisY + yMoved;

                    // Set the label to the new position
                    ImageShape.this.x = nextX;
                    ImageShape.this.y = nextY;
                    imageLabel.setLocation(nextX, nextY);
                    imageLabel.getParent().repaint();
                }
            }
        });
    }
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
        imageLabel.setLocation(x, y);
    }

    private void rotateImage(int angle) {
        if (angle <0){rot -= Math.PI / 2;}
        else{rot+= Math.PI/2;}
        imageLabel.repaint();
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public Image getImage() {
        return new ImageIcon(imageLabel.getIcon().toString()).getImage();
    }
    public void DeleteListener(){
        
            Container parent = imageLabel.getParent();
            parent.remove(imageLabel);
            parent.repaint();
        
    }
    public String getPath(){
        return this.path;
    }
}
