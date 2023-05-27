package com.example.battleship;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GamePlayActivity extends AppCompatActivity {
    final int BOARD_SIZE = 10;
    final int NUMBER_OF_SHIPS = 10;
    final Context context = this;
    Board userBoard;
    Board comBoard;
    BoardView userBoardView;
    BoardView comBoardView;
    ImageView[] userShipViews;

    Ship selectedShip = null;

    ImageView ivArrow;
    Dialog dialog;
    TextView tvEndGame;
    ImageButton ibContinue;
    ImageButton ibMainMenu;
    ImageButton ibReplay;
    ImageButton ibNext;
    Player user;
    ComPlayer com;
    GamePlay game;
    Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        game = new GamePlay();
        game.setUpGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @Override
    public void onBackPressed() {
        game.pauseGame();
    }

    class GamePlay {
        void setUpGame() {
            createBoard();
            createDialog();
            setDisplayParts();
        }

        void startGame() {
            disableBoard(user);
            findViewById(R.id.place_ship_layout).setVisibility(View.GONE);
            findViewById(R.id.com_board).setVisibility(View.VISIBLE);
            ivArrow.setVisibility(View.VISIBLE);

            comBoardView.readyToDraw();
            userBoardView.readyToDraw();
            userBoardView.invalidate();
        }

        void endGame(Player player) {
            if(player == user) {
                tvEndGame.setText(getString(R.string.tv_endGame_win));
            } else {
                tvEndGame.setText(getString(R.string.tv_endGame_lose));
            }
            tvEndGame.setVisibility(View.VISIBLE);
            ibContinue.setVisibility(View.GONE);
            dialog.show();
        }

        void pauseGame() {
            tvEndGame.setVisibility(View.GONE);
            ibContinue.setVisibility(View.VISIBLE);
            dialog.show();
        }
        void changeArrowDir() {
            if(ivArrow.getTag().equals("Left")) {
                ivArrow.setImageResource(R.drawable.iv_arrow_right);
                ivArrow.setTag("Right");

            } else if(ivArrow.getTag().equals("Right")) {
                ivArrow.setImageResource(R.drawable.iv_arrow_left);
                ivArrow.setTag("Left");
            }
        }

        void enableComBoard() {
            comBoardView.setOnTouchListener(new ComBoardListener());
        }

        void disableBoard(Player whoseBoard) {
            if(whoseBoard instanceof ComPlayer) {
                comBoardView.setOnTouchListener(null);
            } else {
                userBoardView.setOnTouchListener(null);
            }
        }


    public void createBoard() {
        userBoard = new Board(BOARD_SIZE, NUMBER_OF_SHIPS);
        userBoardView = findViewById(R.id.user_board);
        userBoardView.setBoard(userBoard);
        userBoardView.setOnDragListener(new BoardOnDragListener());
        user = new Player(userBoard, game);
        comBoard = new Board(BOARD_SIZE, NUMBER_OF_SHIPS);
        for(int i = 0; i < NUMBER_OF_SHIPS; i++) {
            comBoard.getShip(i).setInvisible();
        }
        comBoardView = findViewById(R.id.com_board);
        comBoardView.setBoard(comBoard);
        comBoardView.setOnTouchListener(new ComBoardListener());
        com = new ComPlayer(comBoard, game);

        userShipViews = new ImageView[NUMBER_OF_SHIPS];
        for(int i = 0; i < NUMBER_OF_SHIPS; i++) {
            userShipViews[i] = findViewById(R.id.ship_01_00 + i);
            userShipViews[i].setOnTouchListener(new ShipTouchListener(i));
        }
        FunctionListener functionListener = new FunctionListener();
        final ImageButton ibRotate = findViewById(R.id.ib_rotate);
        final ImageButton ibRandom = findViewById(R.id.ib_random);
        ibNext = findViewById(R.id.ib_next);
        ibRotate.setOnClickListener(functionListener);
        ibRandom.setOnClickListener(functionListener);
        ibNext.setOnClickListener((functionListener));
        ivArrow = findViewById(R.id.iv_arrow);
        ivArrow.setTag("Right");

        userBoardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                userBoardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int cellSize = (int)userBoardView.cellSize();
                int boardSize = (int)userBoardView.boardSize();

                for(int i = 0; i < NUMBER_OF_SHIPS; i++) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) userShipViews[i].getLayoutParams();
                    params.height = cellSize;
                    params.width = cellSize * userBoard.getShip(i).getSize();
                    params.rightMargin = cellSize;
                    params.bottomMargin = cellSize;
                    userShipViews[i].setLayoutParams(params);
                }
                resizeView(userBoardView, boardSize, boardSize);
                resizeView(comBoardView, boardSize, boardSize);
                resizeView(ibRotate, (int)(cellSize * 1.5), (int)(cellSize * 1.5));
                resizeView(ibRandom, (int)(cellSize * 1.5), (int)(cellSize * 1.5));
                resizeView(ibNext, (int)(cellSize * 1.5), (int)(cellSize * 1.5));
                resizeView(ivArrow, cellSize, cellSize);
            }

            private void resizeView(View view, int width, int height) {
                ConstraintLayout.LayoutParams params;
                params = (ConstraintLayout.LayoutParams)view.getLayoutParams();
                params.width = width;
                params.height = height;
                view.setLayoutParams(params);
            }
        });
        findViewById(R.id.screen).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                if(event.getAction() == DragEvent.ACTION_DROP) {
                    View v = (View) event.getLocalState();
                    v.setVisibility(View.VISIBLE);
                    return true;
                }
                return true;
            }
        });

    }


    public void setDisplayParts() {
        ImageButton ibRotate = findViewById(R.id.ib_random);
        ImageButton ibRandom = findViewById(R.id.ib_rotate);
        ImageButton ibNext = findViewById(R.id.ib_next);

        FunctionListener funcListener = new FunctionListener();
        ibRotate.setOnClickListener(funcListener);
        ibRandom.setOnClickListener(funcListener);
        ibNext.setOnClickListener(funcListener);

        }
    }

    public void createDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.game_play_dialog);
        dialog.setTitle("Title...");
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        tvEndGame = dialog.findViewById(R.id.tv_endGame);
        tvEndGame.setBackgroundColor(Color.TRANSPARENT);

        ibContinue = dialog.findViewById(R.id.ib_continue);
        ibContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ibMainMenu = dialog.findViewById(R.id.ib_mainMenu);
        ibMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GamePlayActivity.this, MainActivity.class);
                startActivity(intent);
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                GamePlayActivity.this.finish();
            }
        });
        ibReplay = dialog.findViewById(R.id.ib_replay);
        ibReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                recreate();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    public void displayMessage(CharSequence message) {
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(GamePlayActivity.this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public class FunctionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch(id) {
                case R.id.ib_rotate:
                    if(selectedShip != null && selectedShip.isPlaced()) {
                        Cell headCell = selectedShip.rotate();
                        if (!userBoard.placeShip(selectedShip, headCell)) {
                            displayMessage(getString(R.string.tv_guide_rotate_failed));
                        } else {
                            float cellSize = userBoardView.cellSize();
                            RelativeLayout.LayoutParams params;
                            if(selectedShip.isHorizontal()) {
                                params = new RelativeLayout.LayoutParams((int)(cellSize * selectedShip.getSize()), (int)cellSize);
                            } else {
                                params = new RelativeLayout.LayoutParams((int)cellSize, (int)(cellSize * selectedShip.getSize()));
                            }
                            params.leftMargin = (int)(headCell.getX() * cellSize);
                            params.topMargin = (int)(headCell.getY() * cellSize);
                            userShipViews[selectedShip.getId()].setLayoutParams(params);
                            userShipViews[selectedShip.getId()].setImageResource(selectedShip.getImageId());
                        }
                    } else {
                        displayMessage(getString(R.string.tv_guide_rotate_select_ship));
                    }
                    break;
                case R.id.ib_random:
                    selectedShip = null;
                    for(ImageView ship : userShipViews) {
                        ship.setVisibility(View.INVISIBLE);
                        ship.setClickable(false);
                    }
                    userBoard.placeShipRandomly();

                    for(int i = 0; i < NUMBER_OF_SHIPS; i++) {
                        ViewGroup owner = (ViewGroup) userShipViews[i].getParent();
                        owner.removeView(userShipViews[i]);

                        Ship ship = userBoard.getShip(i);
                        Cell headCell = ship.getHeadCell();
                        float cellSize = userBoardView.cellSize();
                        RelativeLayout.LayoutParams params;
                        if(ship.isHorizontal()) {
                            params = new RelativeLayout.LayoutParams((int)(cellSize * ship.getSize()), (int)cellSize);
                        } else {
                            params = new RelativeLayout.LayoutParams((int)cellSize, (int)(cellSize * ship.getSize()));
                        }
                        userShipViews[i].setImageResource(userBoard.getShip(i).getImageId());
                        params.leftMargin = (int)(headCell.getX() * cellSize);
                        params.topMargin = (int)(headCell.getY() * cellSize);
                        userShipViews[i].setLayoutParams(params);
                        userBoardView.addView(userShipViews[i], params);
                        userShipViews[i].setVisibility(View.VISIBLE);
                        ibNext.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.ib_next:
                    if(userBoard.areAllShipsPlaced()) {
                        for(ImageView ship : userShipViews) {
                            ship.setVisibility(View.GONE);
                        }
                        game.startGame();
                    } else {
                        displayMessage(getString(R.string.tv_guide_next_ship_not_placed));
                    }
            }
        }
    }

    private class ComBoardListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            Cell cell = ((BoardView)view).locateCell(event.getX(), event.getY());
            if(action == MotionEvent.ACTION_DOWN) {
                if(cell.isHit()) {
                    return false;
                }
                ShootResult result = user.shoot(cell);
                if(result == ShootResult.HIT) {
                }else if (result == ShootResult.MINE){
                    cell.getShip().sink();
                    game.changeArrowDir();
                    game.disableBoard(com);
                    com.randomlyShoot(user.getBoard(), userBoardView);
                    game.disableBoard(com);
                    game.changeArrowDir();
                    com.randomlyShoot(user.getBoard(), userBoardView);
                }
                else if(result == ShootResult.MISS) {
                    game.changeArrowDir();
                    game.disableBoard(com);
                    com.randomlyShoot(user.getBoard(), userBoardView);
                } else {
                    cell.getShip().sink();
                    if(result == ShootResult.END) {
                        game.endGame(user);
                    }
                }
                view.setClickable(false);
                ((BoardView)view).invalidate();
            }
            return true;
        }
    }

    private class ShipTouchListener implements View.OnTouchListener {
        private int id;

        ShipTouchListener(int id) {
            this.id = id;
        }

        @Override
        public boolean onTouch(View touchedShip, MotionEvent touchEvent) {
            int action = touchEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                selectedShip = userBoard.getShip(id);
                ClipData clipData = ClipData.newPlainText("", "");
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(touchedShip);
                touchedShip.startDrag(clipData, dragShadowBuilder, touchedShip, 0);
                touchedShip.setVisibility(View.INVISIBLE);
                ibNext.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }


    private class BoardOnDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View board, DragEvent event) {
            int action = event.getAction();
            float cellSize = ((BoardView)board).cellSize();
            Cell cell = null;
            float cellX, cellY;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if(selectedShip.isHorizontal()) {
                        cellX = event.getX() - ((float)selectedShip.getSize() / 2.0f - 0.5f) * cellSize;
                        if(cellX < 0) {
                            cellX = 0;
                        }
                        cellY = event.getY();
                    } else {
                        cellX = event.getX();
                        cellY = event.getY() - ((float)selectedShip.getSize() / 2.0f - 0.5f) * cellSize;
                        if(cellY < 0) {
                            cellY = 0;
                        }
                    }
                    cell = ((BoardView)board).locateCell(cellX, cellY);

                    View view = (View) event.getLocalState();

                    if(cell != null && view != null && user.getBoard().placeShip(selectedShip, cell)) {
                        RelativeLayout.LayoutParams originalParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(originalParams.width, originalParams.height);

                        ViewGroup owner = (ViewGroup) view.getParent();
                        owner.removeView(view);

                        params.leftMargin = (int) (cell.getX() * cellSize);
                        params.topMargin = (int) (cell.getY() * cellSize);
                        ((RelativeLayout)board).addView(view, params);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        displayMessage(getText(R.string.tv_guide_place_failed));
                        view.setVisibility(View.VISIBLE);
                    }
                    if(userBoard.areAllShipsPlaced()) {
                        ibNext.setVisibility(View.VISIBLE);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
            }
            return true;
        }
    }

}

