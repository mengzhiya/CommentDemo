package com.mare.comment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mare.comment.R;
import com.mare.comment.adapter.CommentAdapter;
import com.mare.comment.adapter.CommentReplyAdapter;
import com.mare.comment.bean.Comment;
import com.mare.comment.bean.Reply;

public class MainActivity extends Activity {
	private ListView lv_user_comments;
	private Button btn_comment, btn_reply;
	private EditText edt_reply;

	private CommentAdapter commentAdapter;
	private CommentReplyAdapter commentReplyAdapter;

	private static final int ONE_COMMENT_CODE = -1;

	private List<Comment> commentList;
	private List<Reply> replyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initCommentData();
	}

	private void initView() {
		lv_user_comments = (ListView) this.findViewById(R.id.lv_comments);
		btn_comment = (Button) this.findViewById(R.id.btn_main_send);
		commentList = new ArrayList<Comment>();
		btn_comment.setOnClickListener(addCommentListener);
	}

	private void initCommentData() {
		Comment comment = new Comment();
		comment.setUsername("小花");
		comment.setContent("我是一楼");
		commentList.add(comment);

		Comment comment2 = new Comment();
		comment2.setUsername("阿黄");
		comment2.setContent("我是二楼");
		commentList.add(comment2);

		commentReplyAdapter = null;
		commentAdapter = new CommentAdapter(this, commentList,
				replyToCommentListener, commentReplyAdapter,
				replyToReplyListener);
		lv_user_comments.setAdapter(commentAdapter);
	}

	/**
	 * 发表评论的监听
	 */
	private OnClickListener addCommentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onCreateDialog(ONE_COMMENT_CODE, ONE_COMMENT_CODE);
		}
	};

	/**
	 * 回复评论的监听（回复楼主）
	 */
	private OnClickListener replyToCommentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			onCreateDialog(ONE_COMMENT_CODE, position);
		}
	};

	/**
	 * 互相回复的监听（楼中楼）
	 */
	private OnClickListener replyToReplyListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int parentPosition = (Integer) v.getTag(R.id.tag_first);
			int childPosition = (Integer) v.getTag(R.id.tag_second);
			onCreateDialog(parentPosition, childPosition);
		}
	};

	/**
	 * 弹出评论的对话框
	 * 
	 * @param parentPositon
	 *            父节点的position
	 * @param childPostion
	 *            子节点的position
	 * @return
	 */
	protected Dialog onCreateDialog(final int parentPositon,
			final int childPostion) {
		final Dialog customDialog = new Dialog(this);
		LayoutInflater inflater = getLayoutInflater();
		View mView = inflater.inflate(R.layout.dialog_comment, null);
		edt_reply = (EditText) mView.findViewById(R.id.edt_comments);
		btn_reply = (Button) mView.findViewById(R.id.btn_send);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(mView);
		customDialog.show();

		btn_reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (childPostion) {
				case ONE_COMMENT_CODE:
					if (TextUtils.isEmpty(edt_reply.getText().toString())) {
						Toast.makeText(MainActivity.this, "内容不能为空",
								Toast.LENGTH_SHORT).show();
					} else {
						Comment comment = new Comment();
						comment.setUsername("海盗");
						comment.setContent(edt_reply.getText().toString());

						commentList.add(comment);
						commentAdapter.clearList();
						commentAdapter.updateList(commentList);
						commentAdapter.notifyDataSetChanged();
						customDialog.dismiss();
						edt_reply.setText("");
					}
					break;
				default:
					if (TextUtils.isEmpty(edt_reply.getText().toString())) {
						Toast.makeText(MainActivity.this, "内容不能为空",
								Toast.LENGTH_SHORT).show();
					} else {
						Reply reply = new Reply();
						reply.setUsername("兽" + parentPositon + childPostion);
						reply.setContent(edt_reply.getText().toString());

						if (parentPositon != -1) {
							reply.setReplyTo(commentList.get(parentPositon)
									.getReplyList().get(childPostion)
									.getUsername());
							commentList.get(parentPositon).getReplyList()
									.add(reply);
						} else {
							replyList = commentList.get(childPostion)
									.getReplyList();
							replyList.add(reply);
							commentList.get(childPostion).setReplyList(
									replyList);
						}

						commentAdapter.clearList();
						commentAdapter.updateList(commentList);
						commentAdapter.notifyDataSetChanged();
						customDialog.dismiss();
						edt_reply.setText("");
					}
					break;
				}
			}
		});
		return customDialog;
	}

}
