package ru.sfedu.finalqualifyingwork.repository.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sfedu.finalqualifyingwork.model.Comment;
import ru.sfedu.finalqualifyingwork.repository.interfaces.*;
import ru.sfedu.finalqualifyingwork.util.EntityFactory;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.HibernateUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

class HibernateCommentDaoTest {

  private final EntityFactory factory = new EntityFactory(new BCryptPasswordEncoder(12));
  private final HibernateUtil hibernateUtil = new HibernateUtil();
  private final HibernateDataUtil hibernateDataUtil = new HibernateDataUtil(hibernateUtil);
  private final TaskGroupDao taskGroupDao = new HibernateTaskGroupDao(hibernateDataUtil);
  private final GroupDao groupDao = new HibernateGroupDao(hibernateDataUtil, hibernateUtil);
  private final TaskDao taskDao = new HibernateTaskDao(hibernateDataUtil);
  private final UserDao userDao = new HibernateUserDao(hibernateDataUtil);
  private final CommentDao commentDao = new HibernateCommentDao(hibernateDataUtil);

  private void saveComment(Comment comment) {
    assertEquals(Statuses.SUCCESS, userDao.saveUser(comment.getAuthor()));
    assertEquals(Statuses.SUCCESS, userDao.saveUser(comment.getOwner().getAuthor()));
    assertEquals(Statuses.SUCCESS, groupDao.createGroup(comment.getOwner().getOwner().getOwnerGroup()));
    assertEquals(Statuses.SUCCESS, taskGroupDao.createTaskGroup(comment.getOwner().getOwner()));
    assertEquals(Statuses.SUCCESS, taskDao.createTask(comment.getOwner()));
    assertEquals(Statuses.SUCCESS, commentDao.createComment(comment));
  }

  @Test
  void getComment() {
    var commentList = factory.generateComment(10);
    commentList.forEach(comment -> {
      saveComment(comment);
      assertEquals(comment, commentDao.getComment(comment.getId()).orElseThrow());
    });
  }

  @Test
  void createComment() {
    var commentList = factory.generateComment(10);
    commentList.forEach(comment -> {
      saveComment(comment);
      assertEquals(comment, commentDao.getComment(comment.getId()).orElseThrow());
    });
  }

  @Test
  void editComment() {
    var commentList = factory.generateComment(10);
    commentList.forEach(comment -> {
      saveComment(comment);
      assertEquals(comment, commentDao.getComment(comment.getId()).orElseThrow());
      comment.setText(comment.getText() + " edited");
      comment.setEdited(true);
      assertEquals(Statuses.SUCCESS, commentDao.editComment(comment));
      assertEquals(comment, commentDao.getComment(comment.getId()).orElseThrow());
    });
  }

  @Test
  void deleteComment() {
    var commentList = factory.generateComment(10);
    commentList.forEach(comment -> {
      saveComment(comment);
      assertEquals(comment, commentDao.getComment(comment.getId()).orElseThrow());
      assertEquals(Statuses.SUCCESS, commentDao.deleteComment(comment.getId()));
      assertTrue(commentDao.getComment(comment.getId()).isEmpty());
    });
  }
}