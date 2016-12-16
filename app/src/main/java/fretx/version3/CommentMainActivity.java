package fretx.version3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CommentMainActivity extends AppCompatActivity {

    private EditText m_txtMessage;
    private RadioGroup m_group;
    private RadioButton m_rdbtn;
    private Button m_btnSend;
    private String m_selectedOptionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_main);

        m_selectedOptionName = "I have a suggestion";
        m_group = (RadioGroup)findViewById(R.id.radio_group);
        m_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                m_rdbtn = (RadioButton) findViewById(checkedId);
                Toast.makeText(CommentMainActivity.this,m_rdbtn.getText(), Toast.LENGTH_SHORT).show();
                m_selectedOptionName = m_rdbtn.getText().toString();
            }
        });
        m_txtMessage = (EditText)findViewById(R.id.editMessage);
        m_btnSend = (Button)findViewById(R.id.btnSend);
        m_btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iemail = new Intent(Intent.ACTION_SEND);
                iemail.setType("message/rfc822");
                iemail.putExtra(Intent.EXTRA_EMAIL, new String[]{"twlin.ad@gmail.com"});
                iemail.putExtra(Intent.EXTRA_SUBJECT, m_selectedOptionName);
                iemail.putExtra(Intent.EXTRA_TEXT   , m_txtMessage.getText().toString());
                try {
                    startActivityForResult(Intent.createChooser(iemail, "Send mail..."), 123);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

//                try {
//                    GMailSender sender = new GMailSender("buglucky1230@gmail.com", "Father1960Mother825");
//                    sender.sendMail(m_selectedOptionName,
//                            m_txtMessage.getText().toString(),
//                            "buglucky1230@gmail.com",
//                            "twlin.ad@gmail.com");
//                } catch (Exception e) {
//                    Log.e("SendMail", e.getMessage(), e);
//                }
//                Mail m = new Mail("Me@gmail.com", "password");
//                String[] toArr = {"twlin.ad@gmail.com"};
//                m.setTo(toArr);
//                m.setFrom("wooo@wooo.com");
//                m.setSubject(m_selectedOptionName);
//                m.setBody(m_txtMessage.getText().toString());
//
//                try {
//                    m.addAttachment(System.getProperty("user.dir"));
//
//                    if(m.send()) {
//                        Toast.makeText(getApplicationContext(), "Email was sent successfully.", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Email was not sent.", Toast.LENGTH_LONG).show();
//                    }
//                } catch(Exception e) {
//                    Toast.makeText(getApplicationContext(), "There was a problem sending the email.", Toast.LENGTH_LONG).show();
//                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            Intent intent = new Intent(getApplicationContext(), CommentThxActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
