package ru.sfedu.finalqualifyingwork.repository.interfaces;

import ru.sfedu.finalqualifyingwork.model.Comment;
import ru.sfedu.finalqualifyingwork.util.Statuses;

import java.util.Optional;

public interface CommentDao {

  Optional<Comment> getComment(long id);

  Statuses createComment(Comment comment);

  Statuses editComment(Comment comment);

  Statuses deleteComment(long id);

}
