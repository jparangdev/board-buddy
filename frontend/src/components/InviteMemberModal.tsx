import {useEffect, useState} from 'react';
import type {ApiError, GroupMember, User} from '@/types';
import {groupService, userService} from '@/services';
import {useAuth, useDebounce} from '@/hooks';
import styles from './Modal.module.css';

interface Props {
  groupId: number;
  existingMemberIds: number[];
  onClose: () => void;
  onInvited: (member: GroupMember) => void;
}

export function InviteMemberModal({ groupId, existingMemberIds, onClose, onInvited }: Props) {
  const [keyword, setKeyword] = useState('');
  const [searchResults, setSearchResults] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [isInviting, setIsInviting] = useState(false);
  const [error, setError] = useState('');
  const { user: currentUser } = useAuth();

  const debouncedKeyword = useDebounce(keyword, 300);

  useEffect(() => {
    if (!debouncedKeyword.trim()) {
      setSearchResults([]);
      return;
    }

    const search = async () => {
      setIsSearching(true);
      try {
        const users = await userService.searchUsers(debouncedKeyword.trim());
        const filtered = users.filter(
          (u) => u.id !== currentUser?.id && !existingMemberIds.includes(u.id)
        );
        setSearchResults(filtered);
      } catch {
        setSearchResults([]);
      } finally {
        setIsSearching(false);
      }
    };
    search();
  }, [debouncedKeyword, currentUser?.id, existingMemberIds]);

  const handleInvite = async () => {
    if (!selectedUser) return;
    setError('');
    setIsInviting(true);

    try {
      const member = await groupService.inviteMember(groupId, selectedUser.userTag);
      onInvited(member);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Failed to invite member');
    } finally {
      setIsInviting(false);
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>&#x1F465; Invite Member</h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>

        <div className="form-group">
          <label htmlFor="searchUser">Search by nickname</label>
          <input
            id="searchUser"
            type="text"
            className="input"
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setSelectedUser(null);
              setError('');
            }}
            placeholder="Type a nickname to search..."
            autoFocus
          />
          <p className={styles.searchHint}>
            Search for users by their nickname to invite them
          </p>
        </div>

        {isSearching && (
          <p className={styles.noResults}>Searching...</p>
        )}

        {!isSearching && debouncedKeyword.trim() && searchResults.length === 0 && (
          <p className={styles.noResults}>No users found</p>
        )}

        {searchResults.length > 0 && (
          <div className={styles.searchResults}>
            {searchResults.map((user) => (
              <div
                key={user.id}
                className={`${styles.userCard} ${selectedUser?.id === user.id ? styles.selected : ''}`}
                onClick={() => setSelectedUser(user)}
              >
                <div className={styles.userCardAvatar}>
                  {user.nickname.charAt(0).toUpperCase()}
                </div>
                <div className={styles.userCardInfo}>
                  <span className={styles.userCardName}>{user.nickname}</span>
                  <span className={styles.userCardTag}>{user.userTag}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {error && <p className={styles.error}>{error}</p>}

        <div className={styles.actions}>
          <button type="button" className="btn btn-secondary" onClick={onClose}>
            Cancel
          </button>
          <button
            type="button"
            className="btn btn-primary"
            disabled={!selectedUser || isInviting}
            onClick={handleInvite}
          >
            {isInviting ? 'Inviting...' : 'Send Invite'}
          </button>
        </div>
      </div>
    </div>
  );
}
