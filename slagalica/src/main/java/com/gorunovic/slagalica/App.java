package com.gorunovic.slagalica;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class App extends Application
{
	private static ArrayList<String> loadDatabase(Path path)
	{
		ArrayList<String> database = null;
		
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8")))
		{
			database = new ArrayList<>(Integer.parseInt(reader.readLine()));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				database.add(line);
			}
			return database;
		} 
		catch (IOException e)
		{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error");
			alert.setHeaderText("");
			alert.setContentText("IOException - neuspelo otvaranje rečnika");
			alert.setAlertType(AlertType.ERROR);
			alert.showAndWait();
			Platform.exit();
		}
		return database;
	}
	
	private static Parent createContent()
	{		
		// Baza reci
		ArrayList<String> baza = loadDatabase(Paths.get("resources/sr-Latin.dic"));
		//
		
		// Slova
		TextField[] letters = new TextField[12];
		for (int i = 0; i < letters.length; i++)
		{
			letters[i] = new TextField();
			letters[i].setPrefSize(50, 20);
			letters[i].setAlignment(Pos.CENTER);
			letters[i].setFont(Font.font("Verdana", FontWeight.BOLD, 15));
		}
		//

		// TextArea rezultat
		TextArea result = new TextArea();
		result.setPrefSize(360, 50);
		result.setLayoutX(220);
		result.setLayoutY(200);
		result.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		result.setStyle("-fx-text-alignment: center;");
		result.setEditable(false);
		//

		// Dugmad
		Button btnRun = new Button("Reši");
		Button btnReset = new Button("Obriši");

		btnRun.setPrefSize(75, 20);
		btnReset.setPrefSize(75, 20);

		btnRun.setOnAction(e ->
		{
			new Thread(() ->
			{		
				ArrayList<String> slova = new ArrayList<>(12);
				
				for(int i = 0; i < 12; i++)
				{
					slova.add(letters[i].getText().toLowerCase()
							.replace("dž", "1")
							.replace("lj", "2")
							.replace("nj", "3"));
				}
								
				for (int i = 0; i < baza.size(); i++)
				{
					boolean contains = true;
					ArrayList<String> temp = new ArrayList<>(slova);
					String rec = baza.get(i);
					
					for (int j = 0; j < rec.length(); j++)
					{
						String s = rec.substring(j, j + 1);
						contains = contains && temp.remove(s);
					}

					if (contains)
					{
						result.setText(rec
								.replace("1", "dž")
								.replace("2", "lj")
								.replace("3", "nj"));
						slova = null;
						break;
					}
				}	
			}).start();
		});

		btnReset.setOnAction(e ->
		{
			new Thread(() ->
			{
				for (int i = 0; i < letters.length; i++)
				{
					letters[i].clear();
				}

				result.clear();
			}).start();
		});
		//

		// HBox za slova
		HBox lettersBox = new HBox(10);
		lettersBox.getChildren().addAll(letters);
		lettersBox.setAlignment(Pos.CENTER);
		lettersBox.setPrefSize(740, 50);
		lettersBox.setLayoutX(30);
		lettersBox.setLayoutY(50);
		lettersBox.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		//

		// HBox za dugmad
		HBox buttonsBox = new HBox(10);
		buttonsBox.getChildren().addAll(btnReset, btnRun);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.setPrefSize(180, 50);
		buttonsBox.setLayoutX(590);
		buttonsBox.setLayoutY(350);
		//

		// Glavni element
		Pane root = new Pane();
		root.getChildren().addAll(lettersBox, buttonsBox, result);
		root.setStyle("-fx-background-color: #eeeeff;");
		//

		return root;
	}

	@Override
	public void start(Stage stage)
	{
		try
		{
			stage.setScene(new Scene(createContent(), 800, 400));
			stage.setResizable(false);
			stage.setTitle("Slagalica");
			stage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
