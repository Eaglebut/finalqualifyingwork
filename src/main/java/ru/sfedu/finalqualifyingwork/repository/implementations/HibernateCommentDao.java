package ru.sfedu.finalqualifyingwork.repository.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.finalqualifyingwork.model.Comment;
import ru.sfedu.finalqualifyingwork.repository.interfaces.CommentDao;
import ru.sfedu.finalqualifyingwork.util.HibernateDataUtil;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateCommentDao implements CommentDao {

  private final HibernateDataUtil hibernateDataUtil;


  @Override
  public Optional<Comment> getComment(long id) {
    return hibernateDataUtil.getEntityById(Comment.class, id);
  }

  @Override
  public Statuses createComment(Comment comment) {
    comment.setCreated(new Date());
    comment.setLastUpdated(new Date());
    return hibernateDataUtil.createEntity(comment);
  }

  @Override
  public Statuses editComment(Comment comment) {
    comment.setLastUpdated(new Date());
    comment.setEdited(true);
    return hibernateDataUtil.updateEntity(comment);
  }

  @Override
  public Statuses deleteComment(long id) {
    return hibernateDataUtil.deleteEntity(Comment.class, id);
  }
}
