package esprit.tn.guiproject.connection.service;

import java.util.List;

public interface ICrud<T>
{
   void ajouter (T t) throws Exception;
   void supprimer (T t) throws Exception;



   void modifier (T t) throws Exception;
   List<T> getAll () throws Exception;


}
