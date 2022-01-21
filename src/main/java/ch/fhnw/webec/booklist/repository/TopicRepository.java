package ch.fhnw.webec.booklist.repository;

import ch.fhnw.webec.booklist.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> { }
