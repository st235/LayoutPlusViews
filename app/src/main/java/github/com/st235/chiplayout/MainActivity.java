package github.com.st235.chiplayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Px
    private static final int PROFILE_PICTURE_SIZE = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final AppCompatImageView feedImage = findViewById(R.id.insta_image);
        feedImage.setImageBitmap(BitmapHelper.decodeSampledBitmapFromResource(getResources(),
                R.drawable.cat2, PROFILE_PICTURE_SIZE, PROFILE_PICTURE_SIZE));

        final ChipLayout tagsChipLayout = findViewById(R.id.tag_layout);
        String[] tags = getResources().getStringArray(R.array.cats_tags);

        for (String tag: tags) {
            addChildTag(tagsChipLayout, tag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareArticle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addChildTag(@NonNull ViewGroup tagLayout,
                             @NonNull String tag) {
        TextView tagView =
                new TextView(new ContextThemeWrapper(this, R.style.ChipViewTextAppearance));
        tagView.setText(tag);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(Dimens.dpToPx(2), Dimens.dpToPx(2), Dimens.dpToPx(2), Dimens.dpToPx(2));

        tagLayout.addView(tagView, params);
    }

    private void shareArticle() {
        String shareBody = getString(R.string.share_text);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent,
                getString(R.string.share_chooser)));
    }
}
