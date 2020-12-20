package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int columns_number = 10;
    private int rows_number = 10;
    private int bomb_number = 20;
    private int flag_number = 0;
    private boolean win = false;
    private boolean lose = false;
    private int[][] map;
    private int[][] display;
    private Button[][] buttons;
    private int mode = 1;
    private static int COVER = 1;
    private static int FLAG = 2;
    private static int UNCOVER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initButtons();
        this.initMapAndDisplay();

        Button button_restart = this.findViewById(R.id.button_restart);
        button_restart.setBackgroundColor(getResources().getColor(R.color.green));
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });
        Button button_mode = this.findViewById(R.id.button_mode);
        button_mode.setBackgroundColor(getResources().getColor(R.color.green));
        button_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_mode();
            }
        });
        this.publishToastMessage("If mode button is green, you can check if a case is a bomb or not.", Toast.LENGTH_LONG);
        this.publishToastMessage("When you click on it, it change to red and if you click on board you put a flag (yellow color) on a case.", Toast.LENGTH_LONG);
        this.publishToastMessage("Your goal is to put all flag and flip all case. Good luck !", Toast.LENGTH_LONG);
    }

    private void restart() {
        this.lose = false;
        this.win = false;
        this.initButtons();
        this.initMapAndDisplay();
        this.resetFlag();
        this.publishToastMessage("If mode button is green, you can check if a case is a bomb or not.", Toast.LENGTH_LONG);
        this.publishToastMessage("When you click on it, it change to red and if you click on board you put a flag (yellow color) on a case.", Toast.LENGTH_LONG);
        this.publishToastMessage("Your goal is to put all flag and flip all case. Good luck !", Toast.LENGTH_LONG);
    }

    private void switch_mode() {
        if (this.mode == 1) {
            this.mode = 2;
            Button button_mode = this.findViewById(R.id.button_mode);
            button_mode.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else {
            this.mode = 1;
            Button button_mode = this.findViewById(R.id.button_mode);
            button_mode.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void initButtons() {
        this.buttons = new Button[columns_number][rows_number];
        for (int i = 0; i < columns_number; i++) {
            for (int j = 0; j < rows_number; j++) {
                Button button = this.findViewById(getResources().getIdentifier("case_" + i + "_" + j, "id", getPackageName()));
                button.setTag(i + "_" + j);
                button.setBackgroundColor(getResources().getColor(R.color.black));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = (String) v.getTag();
                        int x = Integer.parseInt(tag.split("_")[0]);
                        int y = Integer.parseInt(tag.split("_")[1]);
                        updateTable(x, y);
                    }
                });
                this.buttons[i][j] = button;
            }
        }
    }

    private void initMapAndDisplay() {
        this.map = new int[columns_number][rows_number];
        this.display = new int[columns_number][rows_number];

        for (int i = 0; i < columns_number; i++) {
            for (int j = 0; j < rows_number; j++) {
                this.map[i][j] = 0;
                this.display[i][j] = COVER;
            }
        }
        this.initBomb(0);
    }

    private void initBomb(int actual_bomb) {
        Random rand = new Random();

        for (int i = 0; i < columns_number; i++) {
            for (int j = 0; j < rows_number; j++) {
                if (rand.nextInt() % 2 == 1 && actual_bomb < this.bomb_number && this.map[i][j] != -1) {
                    this.map[i][j] = -1;
                    this.updateBombRange(i, j);
                    actual_bomb += 1;
                }
            }
        }
        if (actual_bomb < this.bomb_number)
            this.initBomb(actual_bomb);
    }

    private void updateBombRange(int x, int y) {
        this.incrementValue(x - 1, y - 1);
        this.incrementValue(x - 1, y);
        this.incrementValue(x - 1, y + 1);
        this.incrementValue(x, y - 1);
        this.incrementValue(x, y + 1);
        this.incrementValue(x + 1, y - 1);
        this.incrementValue(x + 1, y);
        this.incrementValue(x + 1, y + 1);
    }

    private void incrementValue(int x, int y) {
        if (x >= 0 && x < this.columns_number && y >= 0 && y < this.rows_number)
            if (this.map[x][y] != -1)
                this.map[x][y] += 1;
    }

    private void showAllMine() {
        for (int i = 0; i < this.columns_number; i++) {
            for (int j = 0; j < this.rows_number; j++) {
                if (this.map[i][j] == -1) {
                    this.buttons[i][j].setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        }
    }

    private boolean checkIfMine(int x, int y) {
        if (this.map[x][y] == -1)
            return true;
        return false;
    }

    private boolean checkIfFlag(int x, int y) {
        if (this.display[x][y] == FLAG)
            return true;
        return false;
    }

    private boolean checkIfCover(int x, int y) {
        if (this.display[x][y] == COVER)
            return true;
        return false;
    }

    private void updateFlagField() {
        TextView bomb_value = (TextView) findViewById(R.id.bomb_value);
        bomb_value.setText(String.valueOf(this.flag_number));
        TextView flag_value = (TextView) findViewById(R.id.flag_value);
        flag_value.setText(String.valueOf(this.bomb_number - this.flag_number));
    }

    private void resetFlag() {
        this.flag_number = 0;
        this.updateFlagField();
    }

    private void addFlag() {
        this.flag_number += 1;
        this.updateFlagField();
    }

    private void removeFlag() {
        this.flag_number -= 1;
        this.updateFlagField();
    }

    public void publishToastMessage(String text, int duration) {
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    public boolean checkIfWin() {
        for (int i = 0; i < columns_number; i++) {
            for (int j = 0; j < rows_number; j++) {
                if (this.display[i][j] == COVER)
                    return false;
            }
        }
        return true;
    }

    private boolean checkTimeValueDiffZero(int first, int second) {
        if (first != 0 && second != 0)
            return true;
        return false;
    }

    public void check(int x, int y) {
        int first_value = this.map[x][y];
        if (y + 1 < this.rows_number && !this.checkTimeValueDiffZero(first_value, this.map[x][y + 1]))
            this.checkAround(x, y + 1);
        if (x + 1 < this.columns_number && !this.checkTimeValueDiffZero(first_value, this.map[x + 1][y]))
            this.checkAround(x + 1, y);
        if (y - 1 >= 0 && !this.checkTimeValueDiffZero(first_value, this.map[x][y - 1]))
            this.checkAround(x, y - 1);
        if (x - 1 >= 0 && !this.checkTimeValueDiffZero(first_value, this.map[x - 1][y]))
            this.checkAround(x - 1, y);
    }

    public void checkAround(int x, int y) {
        if (this.display[x][y] == UNCOVER || this.display[x][y] == FLAG)
            return;
        if (this.map[x][y] == -1)
            return;
        this.displayCase(x, y);
        if (this.map[x][y] != 0) {
            return;
        }
        if (y + 1 < this.rows_number)
            this.checkAround(x, y + 1);
        if (x + 1 < this.columns_number)
            this.checkAround(x + 1, y);
        if (y - 1 >= 0)
            this.checkAround(x, y - 1);
        if (x - 1 >= 0)
            this.checkAround(x - 1, y);
    }

    public void displayCase(int x, int y) {
        switch (this.map[x][y]) {
            case (0):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.gray));
                break;
            case (1):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.one_mine));
                break;
            case (2):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.two_mine));
                break;
            case (3):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.three_mine));
                break;
            case (4):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.four_mine));
                break;
            case (5):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.five_mine));
                break;
            case (6):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.six_mine));
                break;
            case (7):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.seven_mine));
                break;
            case (8):
                this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.eight_mine));
                break;
            default:
                break;
        }
        this.display[x][y] = UNCOVER;
    }

    public void updateTable(int x, int y) {
        if (this.win || this.lose) {
            this.publishToastMessage("The game is close. If you want to play again, please click on restart button", Toast.LENGTH_SHORT);
            return;
        }
        switch (this.mode) {
            case (1):
                if (this.checkIfFlag(x, y))
                    break;
                else if (this.checkIfMine(x, y)) {
                    this.showAllMine();
                    this.publishToastMessage("You lost. If you want to play again, please click on restart button", Toast.LENGTH_LONG);
                    this.lose = true;
                }
                else {
                    this.displayCase(x, y);
                    this.check(x, y);
                }
                break;
            case (2):
                if (this.checkIfFlag(x, y)) {
                    this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.black));
                    this.display[x][y] = COVER;
                    this.removeFlag();
                    break;
                }
                else if (this.checkIfCover(x, y) && this.flag_number < this.bomb_number) {
                    this.buttons[x][y].setBackgroundColor(getResources().getColor(R.color.yellow));
                    this.display[x][y] = FLAG;
                    this.addFlag();
                }
                break;
            default:
                break;
        }
        if (!this.lose) {
            if (this.checkIfWin()) {
                this.win = true;
                this.publishToastMessage("CONGRATULATION ! You won. If you want to play again, please click on restart button", Toast.LENGTH_LONG);
            }
        }
    }
}