package io.pnResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.BaseReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;

public class PNSimulationResultCSVReader extends BaseReader<SimulationResult> {
	private final Logger logger = Logger.getRootLogger();
	private final Pathway pathway;
	private final SimulationResult result;

	public PNSimulationResultCSVReader(final File file, final Pathway pathway, final SimulationResult result) {
		super(file);
		this.pathway = pathway;
		this.result = result;
	}

	public PNSimulationResultCSVReader(final InputStream inputStream, final Pathway pathway,
			final SimulationResult result) {
		super(inputStream);
		this.pathway = pathway;
		this.result = result;
	}

	@Override
	protected SimulationResult internalRead(final InputStream inputStream) throws IOException {
		final CsvSchema schema = CsvSchema.emptySchema().withQuoteChar('"').withColumnSeparator(';').withoutHeader();
		final CsvMapper mapper = new CsvMapper();
		final MappingIterator<String[]> iterator = mapper.readerForArrayOf(String.class)
				.with(CsvParser.Feature.WRAP_AS_ARRAY).with(schema).readValues(inputStream);
		if (!iterator.hasNext()) {
			setHasErrors();
			logger.warn("Simulation result CSV file has no header");
			return null;
		}
		final String[] header = iterator.next();
		int timeColumn = 0;
		final List<BiologicalNodeAbstract> usedBNAs = new ArrayList<>();
		final List<BiologicalEdgeAbstract> usedBEAs = new ArrayList<>();
		final Map<String, Integer> propertyColumnMap = new HashMap<>();
		for (int i = 0; i < header.length; i++) {
			if ("Time".equals(header[i])) {
				timeColumn = i;
			} else {
				final int lastDashIndex = header[i].lastIndexOf('-');
				final String name = lastDashIndex != -1 ? header[i].substring(0, lastDashIndex) : header[i];
				final BiologicalNodeAbstract bna = pathway.getNodeByName(name);
				if (bna != null) {
					usedBNAs.add(bna);
					propertyColumnMap.put(header[i], i);
				} else if (lastDashIndex != -1) {
					final int firstDashIndex = header[i].indexOf('-');
					final String bnaName1 = name.substring(0, firstDashIndex);
					final String bnaName2 = name.substring(firstDashIndex + 1);
					final BiologicalNodeAbstract bna1 = pathway.getNodeByName(bnaName1);
					final BiologicalNodeAbstract bna2 = pathway.getNodeByName(bnaName2);
					if (bna1 != null && bna2 != null) {
						final BiologicalEdgeAbstract edge = pathway.getEdge(bna1, bna2);
						if (edge != null) {
							usedBEAs.add(edge);
							propertyColumnMap.put(header[i], i);
						}
					}
				}
			}
		}
		while (iterator.hasNext()) {
			final String[] row = iterator.next();
			result.addTime(Double.parseDouble(row[timeColumn]));
			for (final BiologicalNodeAbstract bna : usedBNAs) {
				if (bna instanceof Place) {
					addValueFromRow(row, result, bna, SimulationResultController.SIM_TOKEN, "", propertyColumnMap);
				} else if (bna instanceof Transition) {
					addValueFromRow(row, result, bna, SimulationResultController.SIM_ACTIVE, "-active",
							propertyColumnMap);
					addValueFromRow(row, result, bna, SimulationResultController.SIM_FIRE, "-fire", propertyColumnMap);
					addValueFromRow(row, result, bna, SimulationResultController.SIM_ACTUAL_FIRING_SPEED, "-speed",
							propertyColumnMap);
					addValueFromRow(row, result, bna, SimulationResultController.SIM_DELAY, "-delay",
							propertyColumnMap);
				}
			}
			for (final BiologicalEdgeAbstract bea : usedBEAs) {
				addValueFromRow(row, result, bea, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, "-token",
						propertyColumnMap);
				addValueFromRow(row, result, bea, SimulationResultController.SIM_SUM_OF_TOKEN, "-tokenSum",
						propertyColumnMap);
			}
		}
		return result;
	}

	private void addValueFromRow(final String[] row, final SimulationResult result, final BiologicalNodeAbstract bna,
			final int key, final String suffix, final Map<String, Integer> propertyColumnMap) {
		final String propertyName = bna.getName() + suffix;
		final Integer column = propertyColumnMap.get(propertyName);
		if (column != null) {
			// Load null value, otherwise values are shifted in time
			final Double value = StringUtils.isEmpty(row[column]) ? null : Double.parseDouble(row[column]);
			result.addValue(bna, key, value);
		}
	}

	private void addValueFromRow(final String[] row, final SimulationResult result, final BiologicalEdgeAbstract bea,
			final int key, final String suffix, final Map<String, Integer> propertyColumnMap) {
		final String propertyName = bea.getFrom().getName() + '-' + bea.getTo().getName() + suffix;
		final Integer column = propertyColumnMap.get(propertyName);
		if (column != null) {
			// Load null value, otherwise values are shifted in time
			final Double value = StringUtils.isEmpty(row[column]) ? null : Double.parseDouble(row[column]);
			result.addValue(bea, key, value);
		}
	}
}
