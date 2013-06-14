/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import configurations.gui.ConfigPanel;

/**
 *
 * @author dao
 */
public class MDForceLayoutConfig extends ConfigPanel {

    public MDForceLayoutConfig() {
        super(MDForceLayout.class);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.spinners = new ArrayList();
        MigLayout layout = new MigLayout(
                "insets 20",
                "[left, grow 70]20[left, grow 30, fill]",
                "");
        setLayout(layout);
        for (int i = 0; i < MDForceLayoutConfig.pnames.length; i++) {
            String pname = pnames[i];
            Number v = params[i];
            Number min = min_params[i];
            Number max = max_params[i];
            addSpinner(pname, v, min, max, i);
        }
        int row = params.length;
        jCheckBoxRunOnce = new JCheckBox(null, null, MDForceLayoutConfig.RUNONCE);
        jCheckBoxRegardVertexDim = new JCheckBox(null, null, MDForceLayoutConfig.CONCERN_VERTEX_BOUNDS);
        jCheckBoxRunOnce.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                RUNONCE = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        jCheckBoxRegardVertexDim.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                CONCERN_VERTEX_BOUNDS = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        this.add(new JLabel("RegardVertexLabel"), "cell 0 " + row);
        this.add(jCheckBoxRegardVertexDim, "cell 1 " + row);
        this.add(new JLabel("RunOnce"), "cell 0 " + row + 1);
        this.add(jCheckBoxRunOnce, "cell 1 " + row + 1);
    }

    private void addSpinner(String name, Number v, Number min, Number max, int row) {
        Number stepSize = null;
        if (v instanceof Float || v instanceof Double) {
            stepSize = (max.doubleValue() - min.doubleValue()) / 100;
        } else {
            stepSize = (max.longValue() - min.longValue()) / 100;
        }
        JSpinner spinner = new JSpinner();
        JLabel label = new JLabel(name);
        spinner.setModel(
                new SpinnerNumberModel(
                v, (Comparable) min, (Comparable) max, stepSize));
        this.spinners.add(spinner);
        this.add(label, "cell 0 " + row);
        this.add(spinner, "cell 1 " + row);
    }

    private void loadValues() {
        for (int i = 0; i < spinners.size(); i++) {
            spinners.get(i).setValue(MDForceLayoutConfig.params[i]);
        }
        jCheckBoxRegardVertexDim.setSelected(CONCERN_VERTEX_BOUNDS);
        jCheckBoxRunOnce.setSelected(RUNONCE);
    }

    @Override
	public void resetValues() {
        for (int i = 0; i < params.length; i++) {
            params[i] = default_params[i];
        }
        CONCERN_VERTEX_BOUNDS = true;
        RUNONCE = false;
        loadValues();
    }

    @Override
	public void setValues() {
        for (int i = 0; i < MDForceLayoutConfig.params.length; i++) {
            params[i] = (Number) this.spinners.get(i).getValue();
        }
        CONCERN_VERTEX_BOUNDS = jCheckBoxRegardVertexDim.isSelected();
        RUNONCE = jCheckBoxRunOnce.isSelected();
    }
    public static int DEFAULT_BEGIN_ENLARGE = 1000;
    public static int DEFAULT_END_ENLARGE = 2000;
    public static int DEFAULT_MAX_ITERATIONS = 3000;
    public static int MIN_BEGIN_ENLARGE = 100;
    public static int MIN_END_LARGE = 400;
    public static int MIN_MAX_ITERATIONS = 1000;
    public static int MAX_BEGIN_ENLARGE = 2500;
    public static int MAX_ENDLARGE = 4500;
    public static int MAX_MAX_ITERATIONS = 9000;
    public static final float DEFAULT_GRAV_CONSTANT = -0.8f;
    public static final float DEFAULT_MIN_GRAV_CONSTANT = -10f;
    public static final float DEFAULT_MAX_GRAV_CONSTANT = 10f;
    public static final float DEFAULT_DISTANCE = -1f;
    public static final float DEFAULT_MIN_DISTANCE = -1f;
    public static final float DEFAULT_MAX_DISTANCE = 500f;
    public static final float DEFAULT_THETA = 0.9f;
    public static final float DEFAULT_MIN_THETA = 0.0f;
    public static final float DEFAULT_MAX_THETA = 1.0f;
    public static final float DEFAULT_SPRING_COEFF = 1E-4f;
    public static final float DEFAULT_MAX_SPRING_COEFF = 0.1f;
    public static final float DEFAULT_MIN_SPRING_COEFF = 1E-6f;
    public static final float DEFAULT_SPRING_LENGTH = 50;
    public static final float DEFAULT_MIN_SPRING_LENGTH = 0;
    public static final float DEFAULT_MAX_SPRING_LENGTH = 200;
    public static final float DEFAULT_DRAG_COEFF = 0.01f;
    public static final float DEFAULT_MIN_DRAG_COEFF = 0.0f;
    public static final float DEFAULT_MAX_DRAG_COEFF = 0.1f;
    public static final float DEFAULT_MIN_PERTUBATE_RATE = 0.0f;
    public static final float DEFAULT_PERTUBATE_RATE = 0.2f;
    public static final float DEFAULT_MAX_PERTUBATE_RATE = 0.5f;
    public static final float DEFAULT_MIN_REJECT_COEFF = 5E-7f;
    public static final float DEFAULT_REJECT_COEFF = 5E-5f;
    public static final float DEFAULT_MAX_REJECT_COEFF = 5E-2f;
    public static final float DEFAULT_MIN_REJECT_DISTANCE = 1f;
    public static final float DEFAULT_REJECT_DISTANCE = 10f;
    public static final float DEFAULT_MAX_REJECT_DISTANCE = 100f;
    public static Number params[];
    public static Number min_params[];
    public static Number max_params[];
    public static Number default_params[];
    public static final int GRAVITATIONAL_CONST = 0;
    public static final int MIN_DISTANCE = 1;
    public static final int BARNES_HUT_THETA = 2;
    public static final int SPRING_COEFF = 3;
    public static final int SPRING_LENGTH = 4;
    public static final int DRAG_COEFF = 5;
    public static final int PERTUBATE_RATE = 6;
    public static final int REJECT_DISTANCE = 7;
    public static final int REJECT_COEFF = 8;
    public static final int BEGIN_ENLARGE = 9;
    public static final int END_ENLARGE = 10;
    public static final int MAX_ITERATIONS = 11;
    
    public static boolean RUNONCE=false;
    public static boolean CONCERN_VERTEX_BOUNDS = true;
    private static String[] pnames;
    private List<JSpinner> spinners;
    private List<JCheckBox> checkers;
    private JCheckBox jCheckBoxRunOnce,  jCheckBoxRegardVertexDim;
    

    static {
        default_params = new Number[]{DEFAULT_GRAV_CONSTANT, DEFAULT_DISTANCE, DEFAULT_THETA,
                    DEFAULT_SPRING_COEFF, DEFAULT_SPRING_LENGTH,
                    DEFAULT_DRAG_COEFF, DEFAULT_PERTUBATE_RATE,
                    DEFAULT_REJECT_DISTANCE, DEFAULT_REJECT_COEFF,
                    DEFAULT_BEGIN_ENLARGE, DEFAULT_END_ENLARGE, DEFAULT_MAX_ITERATIONS,
                };
        params = new Number[default_params.length];
        System.arraycopy(default_params, 0, params, 0, default_params.length);
        min_params = new Number[]{DEFAULT_MIN_GRAV_CONSTANT, DEFAULT_MIN_DISTANCE, DEFAULT_MIN_THETA,
                    DEFAULT_MIN_SPRING_COEFF, DEFAULT_MIN_SPRING_LENGTH,
                    DEFAULT_MIN_DRAG_COEFF, DEFAULT_MIN_PERTUBATE_RATE,
                    DEFAULT_MIN_REJECT_DISTANCE, DEFAULT_MIN_REJECT_COEFF,
                    MIN_BEGIN_ENLARGE, MIN_END_LARGE, MIN_MAX_ITERATIONS,
                };
        max_params = new Number[]{DEFAULT_MAX_GRAV_CONSTANT, DEFAULT_DISTANCE, DEFAULT_THETA,
                    DEFAULT_MAX_SPRING_COEFF, DEFAULT_MAX_SPRING_LENGTH,
                    DEFAULT_MAX_DRAG_COEFF, DEFAULT_MAX_PERTUBATE_RATE,
                    DEFAULT_MAX_REJECT_DISTANCE, DEFAULT_MAX_REJECT_COEFF,
                    MAX_BEGIN_ENLARGE, MAX_ENDLARGE, MAX_MAX_ITERATIONS
                };
        pnames = new String[]{"GravitationalConstant",
                    "Distance", "BarnesHutTheta",
                    "SpringCoefficient",
                    "DefaultSpringLength", "DragCoefficient",
                    "PertubateRate", "RejectDistance", "RejectCofficient",
                    "BeginEnlarge", "EndEnlarge", "MaxIterations"
                };

    }
}
