package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;

import Nicellipse.src.nicellipse.component.NiEllipse;
import Nicellipse.src.nicellipse.component.NiSpace;
import data.Balise;
import data.EtatBalise;
import data.Position;
import data.Satellite;
import data.Simulation;

/** Draws the model objects directly. Only pixel coordinates and colours belong here. */
public final class SimulationPanel extends NiSpace {
    private static final long serialVersionUID = 1L;
    private static final Color AVAILABLE = new Color(92, 225, 166);
    private static final Color BUSY = new Color(255, 190, 92);
    private static final Color LINK = new Color(92, 240, 205);
    private final Simulation simulation;
    private final NiEllipse earth = new NiEllipse();
    private final Map<Satellite, NiEllipse> satellites = new LinkedHashMap<>();
    private final Map<Balise, NiEllipse> balises = new LinkedHashMap<>();

    public SimulationPanel(Simulation simulation) {
        super("Satellites et balises", new Dimension(1060, 690));
        this.simulation = simulation;
        setBackground(new Color(15, 24, 42));
        earth.setBackground(new Color(35, 115, 175));
        earth.setBorderColor(new Color(112, 200, 233));
        earth.setLayout(new BorderLayout());
        JLabel label = new JLabel("Earth", JLabel.CENTER);
        label.setForeground(Color.WHITE);
        earth.add(label, BorderLayout.CENTER);
        add(earth);
        // The model's entity lists are fixed for the lifetime of a simulation.
        for (Satellite satellite : simulation.getSatellites()) satellites.put(satellite, addMarker());
        for (Balise balise : simulation.getBalises()) balises.put(balise, addMarker());
    }

    private NiEllipse addMarker() {
        NiEllipse marker = new NiEllipse();
        marker.setBorderColor(Color.WHITE);
        add(marker);
        return marker;
    }

    public void refresh() {
        doLayout();
        repaint();
    }

    private int orbitalHeight() { return (int) (getHeight() * 0.56); }
    private int surfaceY() { return orbitalHeight() + 48; }
    private int waterHeight() { return Math.max(0, getHeight() - surfaceY() - 65); }
    private double radiusY() { return Math.max(0, Math.min(getWidth() / 2.0 - 120, orbitalHeight() / 2.0 - 45)); }
    private double radiusX() { return Math.min(radiusY() * 1.8, Math.max(0, getWidth() / 2.0 - 120)); }

    private double orbitScale(Satellite satellite) {
        int index = simulation.getSatellites().indexOf(satellite);
        return 0.5 + 0.5 * index / Math.max(1, simulation.getSatellites().size() - 1);
    }

    /** Schematic orbit: one circuit of the model's horizontal domain is one revolution. */
    public Point getSatelliteCenter(Satellite satellite) {
        double angle = 2 * Math.PI * satellite.getPosition().getX() / simulation.getDomaine().getLargeur();
        double scale = orbitScale(satellite);
        return new Point(getWidth() / 2 + (int) Math.round(radiusX() * scale * Math.cos(angle)),
                orbitalHeight() / 2 - (int) Math.round(radiusY() * scale * Math.sin(angle)));
    }

    public Point getBaliseCenter(Balise balise) {
        Position position = balise.getPosition();
        int x = 65 + (int) Math.round(position.getX() / simulation.getDomaine().getLargeur()
                * Math.max(0, getWidth() - 130));
        int y = surfaceY() + (int) Math.round(position.getProfondeur()
                / simulation.getDomaine().getProfondeurMax() * waterHeight());
        return new Point(x, y);
    }

    @Override
    public void doLayout() {
        int earthSize = (int) (radiusY() * 0.55);
        earth.setBounds((getWidth() - earthSize) / 2, (orbitalHeight() - earthSize) / 2, earthSize, earthSize);
        for (Map.Entry<Satellite, NiEllipse> entry : satellites.entrySet()) {
            Point point = getSatelliteCenter(entry.getKey());
            entry.getValue().setBounds(point.x - 8, point.y - 8, 16, 16);
            entry.getValue().setBackground(entry.getKey().estDisponible() ? AVAILABLE : BUSY);
        }
        for (Map.Entry<Balise, NiEllipse> entry : balises.entrySet()) {
            Point point = getBaliseCenter(entry.getKey());
            entry.getValue().setBounds(point.x - 8, point.y - 8, 16, 16);
            entry.getValue().setBackground(stateColor(entry.getKey().getEtat()));
        }
    }

    private static Color stateColor(EtatBalise state) {
        switch (state) {
            case REMONTEE: return BUSY;
            case ATTENTE: return new Color(213, 180, 255);
            case TRANSMISSION: return LINK;
            default: return new Color(129, 204, 255);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g);
        drawConnections(g);
        drawLabels(g);
        g.dispose();
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(57, 76, 103));
        for (Satellite satellite : simulation.getSatellites()) {
            double rx = radiusX() * orbitScale(satellite);
            double ry = radiusY() * orbitScale(satellite);
            g.draw(new Ellipse2D.Double(getWidth() / 2.0 - rx, orbitalHeight() / 2.0 - ry, rx * 2, ry * 2));
        }
        g.setColor(new Color(13, 49, 70));
        g.fillRect(0, orbitalHeight(), getWidth(), getHeight() - orbitalHeight());
        for (int tick = 0; tick <= 4; tick++) {
            int y = surfaceY() + waterHeight() * tick / 4;
            g.setColor(tick == 0 ? new Color(104, 200, 227) : new Color(33, 75, 95));
            g.drawLine(52, y, getWidth() - 12, y);
            g.setColor(new Color(164, 196, 215));
            g.drawString(Math.round(simulation.getDomaine().getProfondeurMax() * tick / 4) + " m", 8, y + 4);
        }
        g.setColor(new Color(192, 214, 229));
        g.drawString("ORBITAL SCHEMATIC | Green: disponible | Orange: occupe", 18, 24);
        g.drawString("WATER | x: 0–" + Math.round(simulation.getDomaine().getLargeur()) + " m",
                18, orbitalHeight() + 24);
        g.drawString("COLLECTE → REMONTEE → ATTENTE → TRANSMISSION | Dashed lines: active connections",
                18, getHeight() - 12);
    }

    private void drawConnections(Graphics2D g) {
        g.setColor(LINK);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10, new float[] {8, 6}, 0));
        for (Balise balise : simulation.getBalises()) {
            Satellite satellite = balise.getSatellite();
            if (satellite != null) {
                Point from = getBaliseCenter(balise);
                Point to = getSatelliteCenter(satellite);
                g.drawLine(from.x, from.y, to.x, to.y);
            }
        }
        g.setStroke(new BasicStroke(1));
    }

    private void drawLabels(Graphics2D g) {
        List<Rectangle> occupied = new ArrayList<>();
        occupied.add(earth.getBounds());
        for (NiEllipse marker : satellites.values()) occupied.add(marker.getBounds());
        for (Satellite satellite : simulation.getSatellites()) {
            g.setColor(satellite.estDisponible() ? AVAILABLE : BUSY);
            String text = satellite.getId() + " | " + (satellite.estDisponible() ? "Disponible" : "Occupe");
            Rectangle bounds = labelBounds(g, text, getSatelliteCenter(satellite));
            while (overlaps(bounds, occupied) && bounds.y + 2 * bounds.height < orbitalHeight()) {
                bounds.y += bounds.height + 4;
            }
            occupied.add(bounds);
            g.drawString(text, bounds.x, bounds.y + g.getFontMetrics().getAscent());
        }
        for (Balise balise : simulation.getBalises()) {
            Point center = getBaliseCenter(balise);
            g.setColor(stateColor(balise.getEtat()));
            String text = balise.getId() + " | " + balise.getEtat();
            Rectangle bounds = labelBounds(g, text, center);
            g.drawString(text, bounds.x, bounds.y + g.getFontMetrics().getAscent());
            g.setColor(new Color(192, 214, 229));
            Position position = balise.getPosition();
            String details = "x=" + Math.round(position.getX()) + " m | depth="
                    + Math.round(position.getProfondeur()) + " m";
            Rectangle detailBounds = labelBounds(g, details, new Point(center.x, center.y + bounds.height));
            g.drawString(details, detailBounds.x, detailBounds.y + g.getFontMetrics().getAscent());
        }
    }

    private Rectangle labelBounds(Graphics2D g, String text, Point center) {
        int width = g.getFontMetrics().stringWidth(text);
        int x = Math.max(4, Math.min(center.x - width / 2, getWidth() - width - 4));
        return new Rectangle(x, center.y + 12, width, g.getFontMetrics().getHeight());
    }

    private boolean overlaps(Rectangle bounds, List<Rectangle> occupied) {
        for (Rectangle rectangle : occupied) {
            if (rectangle.intersects(bounds)) return true;
        }
        return false;
    }

    @Override
    protected void paintChildren(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintChildren(g);
        g.dispose();
    }
}
