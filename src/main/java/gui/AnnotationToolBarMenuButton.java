package gui;

import gui.annotation.AnnotationPainter;

import java.awt.*;

public class AnnotationToolBarMenuButton extends ToolBarMenuButton {
	private final ToolBarButton selectFillColor;
	private final ToolBarButton selectTextColor;
	private final ToolBarButton rectangleTool;
	private final ToolBarButton ellipseTool;
	private final ToolBarButton textTool;

	public AnnotationToolBarMenuButton() {
		super(ImagePath.getInstance().getImageIcon("annotation.svg"), new GridLayout(2, 3, 4, 4));
		setToolTipText("Annotation Tools");
		selectFillColor = ToolBarButton.create("comparison.png", "Select Fill Color", this::onSelectFillColorClicked);
		selectTextColor = ToolBarButton.create("font.png", "Select Text Color", this::onSelectTextColorClicked);
		rectangleTool = ToolBarButton.create("rectangle.png", "Select Fill Color", this::onRectangleToolClicked);
		ellipseTool = ToolBarButton.create("ellipse.png", "Select Text Color", this::onEllipseToolClicked);
		textTool = ToolBarButton.create("text.png", "Select Text Color", this::onTextToolClicked);

		addMenuButton(selectFillColor);
		addMenuButton(rectangleTool);
		addMenuButton(ellipseTool);
		addMenuButton(selectTextColor);
		addMenuButton(textTool);
	}

	private void onSelectFillColorClicked() {
		final Color color = AnnotationPainter.getInstance().chooseColor(AnnotationPainter.getInstance().getFillColor());
		AnnotationPainter.getInstance().setFillColor(color);
	}

	private void onSelectTextColorClicked() {
		final Color color = AnnotationPainter.getInstance().chooseColor(AnnotationPainter.getInstance().getTextColor());
		AnnotationPainter.getInstance().setTextColor(color);
	}

	private void onRectangleToolClicked() {
		AnnotationPainter.getInstance().setCurrentRangeType(AnnotationPainter.RECTANGLE);
	}

	private void onEllipseToolClicked() {
		AnnotationPainter.getInstance().setCurrentRangeType(AnnotationPainter.ELLIPSE);
	}

	private void onTextToolClicked() {
		AnnotationPainter.getInstance().setCurrentRangeType(AnnotationPainter.TEXT);
	}
}
