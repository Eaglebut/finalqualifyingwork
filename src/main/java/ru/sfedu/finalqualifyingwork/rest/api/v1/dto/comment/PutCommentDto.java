package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Comment;

@Data
@NoArgsConstructor
@ApiModel
public class PutCommentDto {

  private String text;

  public PutCommentDto(Comment comment) {
    text = comment.getText();
  }

  public Comment toComment() {
    Comment comment = new Comment();
    comment.setText(text);
    return comment;
  }
}
