package Michaelsoft_Binbows.services;

import Michaelsoft_Binbows.data.ConfiguracionRepository;
import Michaelsoft_Binbows.entities.Configuracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    private final String LIMITE_TOP_KEY = "top_limite";

    /**
     * Obtiene el límite guardado en la BD. 
     * Si no existe, devuelve 10 por defecto.
     */
    public int getLimiteTop() {
        Configuracion config = configuracionRepository.findById(LIMITE_TOP_KEY)
                                      .orElse(new Configuracion(LIMITE_TOP_KEY, "10"));
        return Integer.parseInt(config.getValor());
    }

    /**
     * Guarda el nuevo límite en la BD.
     */
    public void setLimiteTop(int limite) {
        Configuracion config = new Configuracion(LIMITE_TOP_KEY, String.valueOf(limite));
        configuracionRepository.save(config);
    }
}