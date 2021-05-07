package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Comment;

@Data
@NoArgsConstructor
@ApiModel
public class PostCommentDto {

  private String text;
  private long baseCommentId;

  public PostCommentDto(Comment comment) {
    text = comment.getText();
    if (comment.getBaseComment() != null) {
      baseCommentId = comment.getBaseComment().getId();
    }
  }

  public Comment toComment() {
    Comment comment = new Comment();
    comment.setText(text);
    return comment;
  }
}
