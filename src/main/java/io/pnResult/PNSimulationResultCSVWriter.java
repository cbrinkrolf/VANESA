package io.pnResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.BaseWriter;
import petriNet.SimulationResult;
import petriNet.SimulationResultSeriesKey;

public class PNSimulationResultCSVWriter extends BaseWriter<SimulationResult> {
	private final Pathway pw;

	public PNSimulationResultCSVWriter(final File file, final Pathway pw) {
		super(file);
		this.pw = pw;
	}

	@Override
	protected void internalWrite(final OutputStream outputStream, final SimulationResult simRes) throws Exception {
		final List<Place> places = new ArrayList<>();
		final List<Transition> transitions = new ArrayList<>();
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place && !bna.isLogical()) {
				places.add((Place) bna);
			} else if (bna instanceof Transition && !bna.isLogical()) {
				transitions.add((Transition) bna);
			}
		}
		final List<BiologicalEdgeAbstract> edges = new ArrayList<>(pw.getAllEdgesSortedByID());
		final String[] headers = getHeaders(places, transitions, edges);
		// Build csv writer
		final CsvSchema schema = CsvSchema.emptySchema().withQuoteChar('"').withColumnSeparator(';');
		final CsvMapper mapper = CsvMapper.builder().enable(CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS).enable(
				CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS).build();
		final Writer writer = new OutputStreamWriter(outputStream);
		final SequenceWriter csvWriter = mapper.writer(schema).writeValues(writer);
		// Start writing header and data rows
		csvWriter.write(headers);
		for (int t = 0; t < simRes.getTime().size(); t++) {
			final List<String> row = new ArrayList<>();
			row.add(String.valueOf(simRes.getTime().get(t)));
			for (final Place place : places) {
				addValueToRow(row, simRes, place, SimulationResultSeriesKey.PLACE_TOKEN, t);
			}
			for (final Transition transition : transitions) {
				if (transition instanceof DiscreteTransition) {
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.ACTIVE, t);
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.FIRE, t);
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.DELAY, t);
				} else if (transition instanceof StochasticTransition) {
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.ACTIVE, t);
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.FIRE, t);
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.DELAY, t);
				} else if (transition instanceof ContinuousTransition) {
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.FIRE, t);
					addValueToRow(row, simRes, transition, SimulationResultSeriesKey.ACTUAL_FIRING_SPEED, t);
				}
			}
			for (final BiologicalEdgeAbstract edge : edges) {
				addValueToRow(row, simRes, edge, SimulationResultSeriesKey.ARC_ACTUAL_TOKEN_FLOW, t);
				addValueToRow(row, simRes, edge, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN, t);
			}
			csvWriter.write(row.toArray(new String[0]));
		}
	}

	private String[] getHeaders(final List<Place> places, final List<Transition> transitions,
			final List<BiologicalEdgeAbstract> edges) {
		final List<String> headers = new ArrayList<>();
		headers.add("Time");
		for (final Place item : places) {
			headers.add(item.getName());
		}
		for (final Transition t : transitions) {
			if (t instanceof DiscreteTransition) {
				headers.add(t.getName() + "-active");
				headers.add(t.getName() + "-fire");
				headers.add(t.getName() + "-delay");
			} else if (t instanceof StochasticTransition) {
				headers.add(t.getName() + "-active");
				headers.add(t.getName() + "-fire");
				headers.add(t.getName() + "-delay");
			} else if (t instanceof ContinuousTransition) {
				headers.add(t.getName() + "-fire");
				headers.add(t.getName() + "-speed");
			}
		}
		for (final BiologicalEdgeAbstract bea : edges) {
			headers.add(bea.getFrom().getName() + '-' + bea.getTo().getName() + "-token");
			headers.add(bea.getFrom().getName() + '-' + bea.getTo().getName() + "-tokenSum");
		}
		return headers.toArray(new String[0]);
	}

	private void addValueToRow(final List<String> row, final SimulationResult simRes, final GraphElementAbstract gea,
			final SimulationResultSeriesKey key, final int t) {
		if (simRes.contains(gea, key)) {
			row.add(String.valueOf(simRes.get(gea, key).get(t)));
		} else {
			row.add("");
		}
	}
}
