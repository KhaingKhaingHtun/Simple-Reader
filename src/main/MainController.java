package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private Label lblFilePath;

	@FXML
	private TextArea taContent;

	@FXML
	private TextField tfFileTitle;

	@FXML
	private TreeView<String> treeViewFolder;

	private File curentFile;

	@FXML
	void processClose(ActionEvent event) {
		Platform.exit(); // exit from javafx runtime
		System.exit(0); // exit from jvm (main thread ပါသေသွားတယ်)
	}

	private void save(boolean saveAs) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("/home/elsakhaing"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Text File", "*.txt"));
		if (curentFile != null) {
			if (saveAs) {
				curentFile = fileChooser.showSaveDialog(new Stage());
			}
			BufferedWriter writer = null;

			try {
				writer = new BufferedWriter(new FileWriter(curentFile));
				writer.write(taContent.getText());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void setContentToTextArea(File file) {
		if (file != null) {
			tfFileTitle.setText(file.getName());
			String content = "";
			try {
				content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			taContent.setText(content);
		}
	}

	@FXML
	void processOpenFile(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();

		// set Initial Location
		fileChooser.setInitialDirectory(new File("/home/elsakhaing"));

		// extension filter
		// fileChooser.getExtensionFilters().add(new ExtensionFilter("Text File",
		// "*.txt")); // ကြိုက်တဲ့ extension တွေကို
		// (,) ခံပြီးရေးလို့ရတယ်။
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Text File", "*.txt"));

		// File selectedFile = fileChooser.showOpenDialog(null); //parent window
		// ပေးမထားရင် ကိုယ့် OS ရဲ့ default dialog box နဲ့ပွင့်မယ်။
		Stage stage = new Stage();
		File selectedFile = fileChooser.showOpenDialog(stage); // javafx stage ဖွင့်တာ
		setContentToTextArea(selectedFile);

	}

	private void setMouseClickedEvent(List<File> files) {
		treeViewFolder.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getClickCount() == 2) {
				TreeItem<String> selectedItem = treeViewFolder.getSelectionModel().getSelectedItem();
				for (final File file : files) {
					if (file.getName().equals(selectedItem.getValue())) {
						setContentToTextArea(file);
						lblFilePath.setText(file.getAbsolutePath());
						curentFile = file;
						break;
					}
				}
			}
		});
	}

	@FXML
	void processOpenFiles(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		// set Initial Location
		fileChooser.setInitialDirectory(new File("/home/elsakhaing"));

		// extension filter
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Text File", "*.txt"));
		List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
		if (files != null) {
			TreeItem<String> root = new TreeItem<>("Unknown Folder",
					new ImageView(new Image(getClass().getResourceAsStream("../image/folderIcon1.png"))));
			setChildrenToRoot(files, root);
			setMouseClickedEvent(files);
			treeViewFolder.setRoot(root);

		}

	}

	@FXML
	void processOpenFolder(ActionEvent event) {

		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("/home/elsakhaing"));

		File selectedFolder = directoryChooser.showDialog(new Stage());

		if (selectedFolder.isDirectory()) {
			List<File> fileList = Arrays.asList(selectedFolder.listFiles());
			Image folderIcon = new Image(getClass().getResourceAsStream("../image/folderIcon1.png"));
			TreeItem<String> root = new TreeItem<>(selectedFolder.getName(), new ImageView(folderIcon));
			for (final File file : fileList) {

				if (file.isDirectory()) {
					TreeItem<String> subFolder = new TreeItem<String>(file.getName(), new ImageView(folderIcon));
					setChildrenToRoot(Arrays.asList(file.listFiles()), subFolder);
					root.getChildren().add(subFolder);
					continue;
				}
				TreeItem<String> treeItem = new TreeItem<String>(file.getName(),
						new ImageView(new Image(getClass().getResourceAsStream("../image/fileIcon1.png"))));
				root.getChildren().add(treeItem);
			}
			root.setExpanded(true);
			treeViewFolder.setRoot(root);

			setMouseClickedEvent(fileList);
		}
	}

	private void setChildrenToRoot(List<File> files, TreeItem<String> root) {
		for (final File file : files) {
			root.getChildren().add(new TreeItem<String>(file.getName(),
					new ImageView(new Image(getClass().getResourceAsStream("../image/fileIcon1.png")))));
		}
	}

	@FXML
	void processSave(ActionEvent event) {
		save(false);
	}

	@FXML
	void processSaveAs(ActionEvent event) {
		save(true);
	}

}
