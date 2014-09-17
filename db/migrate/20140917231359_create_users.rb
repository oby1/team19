class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :name
      t.string :avatar_url
      t.string :song_name
      t.string :song_artist
      t.integer :distance

      t.timestamps
    end

    add_index :users, :name
  end
end
