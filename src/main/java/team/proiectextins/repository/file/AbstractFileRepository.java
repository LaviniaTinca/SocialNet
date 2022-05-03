package team.proiectextins.repository.file;


import team.proiectextins.domain.Entity;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.memory.InMemoryRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {

    private final String fileName;


    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    protected abstract String createEntityAsString(E entity);

    protected abstract E extractEntity(List<String> atributes);

    protected void writeToFile(E entity) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Writes data to file
     */
    private void writeAllToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.fileName, false))) {
            Iterable<E> entities = findAll();
            for (E entity : entities) {
                bufferedWriter.write(createEntityAsString(entity));
                bufferedWriter.newLine();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Loads data from file
     */
    private void loadData() {
//        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
//            String line;
//            while((line=bufferedReader.readLine()) != null){
//                List<String> args = Arrays.asList(line.split(";"));
//                E entity = extractEntity(args);
//                super.save(entity);
//            }
//        }catch (FileNotFoundException ex){
//            ex.printStackTrace();
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }
        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line -> {
                E entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public E save(E entity) {
        loadData();
        //entity.setId(nextID());
        E e = super.save(entity);
        if (e == null) {
            //writeToFile(entity);
            writeAllToFile();
        }
        return e;
    }

    @Override
    public E delete(ID id) {
        loadData();
        E e = super.delete(id);
        if (e != null)
            writeAllToFile();
        return e;
    }

    @Override
    public E update(E entity) {
        loadData();
        E updated = super.update(entity);
        if (updated == null) {
            writeAllToFile();
        }
        return updated;
    }
}

