package azul.controller;

import azul.model.Game;
import azul.model.move.*;
import azul.model.tile.Tile;
import azul.view.drawable.Drawable;
import azul.view.drawable.board.FloorLineArrow;
import azul.view.drawable.board.PatternLineArrow;
import azul.view.drawable.table.TableTile;
import azul.view.drawable.factory.FactoryTile;

import java.util.ArrayList;

public class Mediator
{
    // Model part.
    private Game mGame ;

    /**
     * Play moves on user interactions.
     * @param game the model.
     */
    public Mediator(Game game)
    {
        mGame = game ;
    }

    public void onClick(Drawable selected)
    {
        switch (mGame.getState())
        {
            case CHOOSE_TILES :

                // Check if user choose tiles in a factory/table.
                if (selected instanceof FactoryTile
                        && ! mGame.getFactory(((FactoryTile) selected).getFactoryIndex()).isEmpty())
                {
                    chooseInFactory((FactoryTile) selected) ;
                }
                else if (selected instanceof TableTile
                        && ! mGame.isTableEmpty())
                {
                    chooseOnTable((TableTile) selected) ;
                }

                break ;

            case SELECT_ROW :

                // Check if user select a pattern/floor line to put his selected tiles.
                if (selected instanceof PatternLineArrow
                        && ((PatternLineArrow) selected).getPlayerIndex() == mGame.getPlayerIndex())
                {
                    selectPatternLine((PatternLineArrow) selected) ;
                }
                else if (selected instanceof FloorLineArrow
                        && ((FloorLineArrow) selected).getPlayerIndex() == mGame.getPlayerIndex())
                {
                    selectFloorLine() ;
                }
        }
    }

    private void chooseInFactory(FactoryTile selected)
    {
        int factory = selected.getFactoryIndex() ;
        int tile = selected.getTileIndex() ;
        // Get the tiles in the factory.
        ArrayList<Tile> factoryTiles = (ArrayList<Tile>) mGame.getFactory(factory).getTiles().clone() ;
        // Get the tile selected by the user.
        azul.model.tile.Tile tileSelected = mGame.getFactory(factory).getTile(tile) ;
        // And get all the factory tiles of this color.
        ArrayList<azul.model.tile.Tile> tilesSelected = mGame.getFactory(factory).take(tileSelected) ;
        // Play it.
        mGame.playMove(new TakeInFactory(mGame.getPlayer(), tilesSelected,
                mGame.getFactory(factory), factoryTiles)) ;
    }

    private void chooseOnTable(TableTile selected)
    {
        int tile = selected.getTileIndex() ;
        // Get the tiles on the table.
        ArrayList<Tile> tableTiles = (ArrayList<Tile>) mGame.getTilesTable().clone() ;
        // Get the tile selected by the user.
        azul.model.tile.Tile tileSelected = mGame.getInTilesTable(tile) ;
        // And get all the table tiles of this color.
        ArrayList<azul.model.tile.Tile> tilesSelected = mGame.takeOnTable(tileSelected) ;
        // Play it.
        mGame.playMove(new TakeOnTable(mGame.getPlayer(), tilesSelected,
                ! azul.model.tile.Tile.isFirstPlayerMakerTaken(), tableTiles)) ;
    }

    private void selectPatternLine(PatternLineArrow selected)
    {
        // Get the selected row.
        int row = selected.getRowIndex() ;
        // Play it.
        mGame.playMove(new ChoosePatternLine(mGame.getPlayer(), row)) ;
    }

    private void selectFloorLine()
    {
        // Play it.
        mGame.playMove(new ChooseFloorLine(mGame.getPlayer())) ;
    }
}
