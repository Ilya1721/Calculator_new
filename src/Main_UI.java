import javafx.application.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javax.script.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

public class Main_UI extends Application
{
	private GridPane MainLayout;
	private HBox[] Rows;
	private Button[] ExtraButtons;
	private Button LeftBrace;
	private Button RightBrace;
	private Button Delete;
	private Button Clear;
	private Button[][] ButtonTable;
	private String[][] ButtonText;
	private TextField ExpressionField;
	private TextField ResultField;
	private Text ExpressionLabel;
	private Text ResultLabel;
	private Text EditLabel;
	private ScriptEngineManager Manager;
	private ScriptEngine Engine;
	private final double MainWidth = 400;
	private final double MainHeight = 470;
	private final double ButtonWidth = 60;
	private final double ButtonHeight = 60;
	private final int RowsCount = 4;
	private final int ButtonInRowCount = 4;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	public void start(Stage MyStage) 
	{
		try 
		{
			set_widgets();
			set_button_funcs();
			set_script_engine();
			
			MyStage.addEventHandler(KeyEvent.KEY_PRESSED, OnEnterPressed());
			MyStage.setTitle("Calculator");
			MyStage.setScene(new Scene(MainLayout));
			MyStage.setResizable(false);
			MyStage.show();
		}
		catch(Exception exc) 
		{
			System.out.println(exc.toString());
		}
	}
	
	public EventHandler<KeyEvent> OnEnterPressed() 
	{
		return new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					calculate();
				}
			}
		};
	}
	
	
	private void set_widgets() 
	{		
		ExpressionLabel = new Text("Выражение");
		ResultLabel = new Text("Результат");
		ExpressionField = new TextField();
		ResultField = new TextField();
		ResultField.setEditable(false);
		ResultField.setMinWidth(MainWidth - MainWidth * 0.3);
		ExpressionField.setMinWidth(MainWidth - MainWidth * 0.3);
		
		EditLabel = new Text("Редактирование");
		HBox LabelBox = new HBox();
		LabelBox.getChildren().add(EditLabel);
		HBox.setMargin(EditLabel, new Insets(15, 0, 0, 25));
		
		LeftBrace = new Button("(");
		LeftBrace.setMinSize(40, 40);
		RightBrace = new Button(")");
		RightBrace.setMinSize(40, 40);
		Delete = new Button("Стереть");
		Delete.setMinSize(40, 40);
		Clear = new Button("Сброс");
		Clear.setMinSize(40, 40);
		
		ButtonText = new String[RowsCount][ButtonInRowCount];
		ButtonText[0] = new String[] {"1", "2", "3", "/"};
		ButtonText[1] = new String[] {"4", "5", "6", "*"};
		ButtonText[2] = new String[] {"7", "8", "9", "-"};
		ButtonText[3] = new String[] {"+/-", "0", "mod", "+"};
		
		ExtraButtons = new Button[ButtonInRowCount];
		String[] ExtraButtonsStr = new String[] {"MR", "M+", "MC", "="};
		for(int i = 0; i < ButtonInRowCount; ++i) 
		{
			ExtraButtons[i] = new Button();
			ExtraButtons[i].setText(ExtraButtonsStr[i]);
			ExtraButtons[i].setMinSize(ButtonWidth, ButtonHeight);
		}
		
		ButtonTable = new Button[RowsCount][ButtonInRowCount];
		for(int i = 0; i < RowsCount; ++i) 
		{
			for(int j = 0; j < ButtonInRowCount; ++j) 
			{
				ButtonTable[i][j] = new Button();
				ButtonTable[i][j].setText(ButtonText[i][j]);
				ButtonTable[i][j].setMinSize(ButtonWidth, ButtonHeight);
			}
		}
		
		MainLayout = new GridPane();
		MainLayout.setMinSize(MainWidth, MainHeight);
		MainLayout.setPadding(new Insets(10, 10, 10, 10));
		MainLayout.getColumnConstraints().add(new ColumnConstraints(100));
		MainLayout.setVgap(5); 
	    MainLayout.setHgap(5); 
		MainLayout.add(ExpressionLabel, 0, 0);
		MainLayout.add(ExpressionField, 1, 0);
		MainLayout.add(ResultLabel, 0, 1);
		MainLayout.add(ResultField, 1, 1);
		MainLayout.getColumnConstraints().add(new ColumnConstraints(100));
		MainLayout.add(LabelBox, 0, 2);
		MainLayout.add(LeftBrace, 0, 3);
		MainLayout.add(RightBrace, 1, 3);
		MainLayout.add(Delete, 2, 3);
		MainLayout.add(Clear, 3, 3);
		MainLayout.getColumnConstraints().add(new ColumnConstraints(100));
		Rows = new HBox[RowsCount];
		for(int i = 0, j = 8; i < RowsCount; ++i, ++j)
		{
			Rows[i]= new HBox();
			Rows[i].setAlignment(Pos.TOP_CENTER);
			Rows[i].getChildren().addAll(ButtonTable[i]);
			MainLayout.add(Rows[i], 0, j);
			MainLayout.add(ExtraButtons[i], 3, j);
		}
		MainLayout.getColumnConstraints().add(new ColumnConstraints(100));	
	}
	
	private void set_button_funcs() 
	{
		for(int i = 0; i < RowsCount - 1; ++i) 
		{
			for(int j = 0; j < ButtonInRowCount; ++j) 
			{
				final Button ButtonPtr = ButtonTable[i][j];
				ButtonTable[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() 
				{
					public void handle(MouseEvent event) 
					{
						ExpressionField.setText(ExpressionField.getText() + ButtonPtr.getText());
					}
				});
			}
		}
		
		ButtonTable[3][0].setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(ExpressionField.getText().toCharArray()[0] == '-') 
				{
					ExpressionField.setText("+" + ExpressionField.getText(1, ExpressionField.getText().length()));
				}
				else if(ExpressionField.getText().toCharArray()[0] == '+')
				{
					ExpressionField.setText("-" + ExpressionField.getText(1, ExpressionField.getText().length()));
				}
				else 
				{
					ExpressionField.setText("+" + ExpressionField.getText());
				}
			}
		});
		
		ButtonTable[3][1].setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				ExpressionField.setText(ExpressionField.getText() + ButtonTable[3][1].getText());
			}
		});
		
		ButtonTable[3][2].setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				ExpressionField.setText(ExpressionField.getText() + "%");
			}
		});
		
		ButtonTable[3][3].setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				ExpressionField.setText(ExpressionField.getText() + ButtonTable[3][3].getText());
			}
		});
		
		LeftBrace.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				ExpressionField.setText(ExpressionField.getText() + LeftBrace.getText());
			}
		});
		
		RightBrace.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				ExpressionField.setText(ExpressionField.getText() + RightBrace.getText());
			}
		});
		
		Delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(ExpressionField.getText().length() > 0) {
					ExpressionField.setText(ExpressionField.getText(0, ExpressionField.getText().length() - 1));
				}
			}
		});
		
		Clear.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(ExpressionField.getText().length() > 0) {
					ExpressionField.setText("");
				}
			}
		});
		
		ExpressionField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					calculate();
				}
			}
		});
		
		ExtraButtons[3].setOnMouseClicked(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				calculate();
			}
		});
	}
	
	private void calculate() 
	{
		try 
		{
			double result;
			char[] text = ExpressionField.getText().toCharArray();
			if(text[0] == '-') {
				result = Double.parseDouble(Engine.eval(ExpressionField.getText(1, text.length)).toString());
				result *= -1;
			}
			else {
				result = Double.parseDouble(Engine.eval(ExpressionField.getText()).toString());
			}
			ResultField.setText(String.valueOf(result));
		}
		catch(Exception exc) 
		{
			System.out.println(exc.toString());
		}
	}
	
	private void set_script_engine() 
	{
		Manager = new ScriptEngineManager();
		Engine = Manager.getEngineByName("JavaScript");
	}
}
