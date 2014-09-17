class Song < ActiveRecord::Base
  # Validations
  validates :device_id, :name, :artist, :username, presence: true

  # Callbacks
  before_save :remove_old_songs

  private

  def remove_old_songs
    Song.where(device_id: device_id).destroy_all
  end
end
