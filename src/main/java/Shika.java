import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;

import shikabot.command.Command;
import shikabot.command.ExitCommand;
import shikabot.parser.Parser;
import shikabot.task.TaskList;

import shikabot.ui.TextUi;

import shikabot.storage.Storage;

public class Shika {

    public static String path = "data/ShikaTasks.txt";
    public static TextUi ui = new TextUi();
    public static Parser parser = new Parser();

    public Storage storage;
    public TaskList taskList;

    public static void main(String[] args) {
        new Shika().run();
    }

    /**
     * Function that calls other functions to run Shika.
     */
    public void run() {
        try {
            setupShika();
        } catch (FileNotFoundException e) {
            ui.printFileErrorMessage();
        }
        runShikaLoop();
    }

    /**
     * Function that attempts to load tasks from ShikaTasks.txt. If the file or parent directories do not exist,
     * it creates them.
     * @throws FileNotFoundException when ShikaTasks.txt is not found.
     */
    public void setupShika() throws FileNotFoundException {
        ui.printLogo();
        this.storage = new Storage(path);
        ui.printWelcomeMessage(checkForSave());
        this.taskList = new TaskList();
        taskList = storage.loadTasks();
    }

    public boolean checkForSave() {
        boolean hasSave = false;
        try {
            hasSave = storage.setupSave();
        } catch (SecurityException e) {
            ui.printSecurityErrorMessage();
        } catch (Storage.FileErrorException e) {
            ui.printFileErrorMessage();
        }
        return hasSave;
    }

    /**
     * Function that calls getCommand() in a loop to run Shika. Loop can be exited by inputting "bye".
     */
    public void runShikaLoop() {
        Command command;
        do {
            command = parser.parseCommand(ui.getCommand());
            executeCommand(command);
        } while (!isExitCommand(command));
    }

    public boolean isExitCommand(Command command) {
        return command instanceof ExitCommand;
    }

    public void executeCommand(Command command) {
        try {
            command.setData(taskList);
            command.execute();
            storage.saveTasks(taskList);
        } catch (IOException e) {
            ui.printSaveErrorMessage();
        }
    }

}


