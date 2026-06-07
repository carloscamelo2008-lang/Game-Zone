package gamezone.services;

import gamezone.entities.Sale;
import gamezone.entities.VideoGame;
import gamezone.repositories.SaleRepository;
import gamezone.repositories.VideoGameRepository;

import java.util.List;
import java.util.UUID;

public class GameService {

    private final VideoGameRepository gameRepository;
    private final SaleRepository      saleRepository;

    public GameService() {
        this.gameRepository = new VideoGameRepository();
        this.saleRepository = new SaleRepository(gameRepository);
    }

    public void addVideoGame(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (game.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (game.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        gameRepository.save(game);
    }

    public double sellVideoGame(String title, int qty) {
        VideoGame game = gameRepository.findByTitle(title);

        try {
            if (game == null) {
                throw new IllegalArgumentException(
                    "El videojuego '" + title + "' no existe en el catálogo");
            }
            if (qty <= 0) {
                throw new ArithmeticException("La cantidad debe ser mayor a 0");
            }
            if (game.getStock() < qty) {
                throw new ArithmeticException(
                    "Stock insuficiente. Disponible: " + game.getStock() + ", solicitado: " + qty);
            }

            Sellable sellable = (Sellable) game;
            double total = sellable.sell(qty);

            gameRepository.update(game);

            Sale sale = new Sale(
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                game, qty, game.calculateFinalPrice()
            );
            saleRepository.save(sale);

            return total;

        } catch (ClassCastException e) {
            throw new IllegalStateException("El videojuego no soporta ventas: " + e.getMessage());
        }
    }

    public List<VideoGame> getAllVideoGames() {
        return gameRepository.findAll();
    }

    public VideoGame findByTitle(String title) {
        return gameRepository.findByTitle(title);
    }

    public List<VideoGame> findByPlatform(String platform) {
        return gameRepository.findByPlatform(platform);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public boolean updateVideoGame(VideoGame updatedGame) {
        if (updatedGame.getTitle() == null || updatedGame.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (updatedGame.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (updatedGame.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return gameRepository.update(updatedGame);
    }

    public boolean deleteVideoGame(String title) {
        return gameRepository.deleteByTitle(title);
    }

    public int    totalSalesCount() { return saleRepository.count(); }
    public double totalRevenue()    { return saleRepository.totalRevenue(); }
}
